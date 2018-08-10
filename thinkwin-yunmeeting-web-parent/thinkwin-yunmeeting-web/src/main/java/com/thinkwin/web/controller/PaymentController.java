package com.thinkwin.web.controller;

import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.orders.model.OrderStatus;
import com.thinkwin.orders.service.OrderService;
import com.thinkwin.orders.vo.OrderVo;
import com.thinkwin.pay.dto.PaymentVo;
import com.thinkwin.pay.model.PayChannelEnum;
import com.thinkwin.pay.service.PayService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.service.FileUploadCommonService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/pay")
public class PaymentController {
    @Resource
    private SaasUserService consoleUserService;
    @Resource
    private FileUploadCommonService fileUploadCommonService;

	@Resource
	PayService payService;

	@Resource
	private HttpServletRequest request;

	@Resource
	SaasTenantService saasTenantCoreService;
	@Resource
	FileUploadService fileUploadService;
	@Resource
	private OrderService orderService;
	@RequestMapping(value = "qrcode", headers = "Accept=image/jpeg, image/jpg, image/png, image/gif", method = RequestMethod.GET)
	public @ResponseBody
	byte[] getImage(HttpServletResponse response, String tradeNo, Integer size) {
		if(StringUtils.isBlank(tradeNo)){
			return null;
		}

		if(null == size || size < 2 || size > 30){
			size = 6;
		}

		String headerValue = CacheControl.maxAge(30, TimeUnit.SECONDS).getHeaderValue();
		response.addHeader("Cache-Control", headerValue);

		PaymentVo paymentVo = payService.queryPayment(tradeNo);
		if(PayChannelEnum.WEIXIN.getCode().equals(paymentVo.getPayChannel())){
			byte[] imgBuff = BarcodeUtil.encoderQRCode(paymentVo.getPayInfo(), "png", size);

			if(ArrayUtils.isEmpty(imgBuff)){
				return new byte[0];
			}
			return imgBuff;
		}
		return new byte[0];
	}

	@RequestMapping(value = "return")
	public ModelAndView payReturn(@RequestParam("out_trade_no") String outTradeNo){
		PaymentVo vo = payService.querySimplePayment(outTradeNo);
		Map<String, Object> modelInfo = new HashMap<String, Object>();

		ModelAndView modelAndView = new ModelAndView("/pay/paymentState", "model", modelInfo);
		String token = RedisUtil.get("PAY_RETURN_TOKEN_" + outTradeNo);
		if(StringUtils.isNotBlank(token)){
			modelAndView.setViewName("redirect:/order/info?token=" + token + "&orderId=" + vo.getOrderId());
		}
		modelInfo.put("orderId", vo.getOrderId());
		modelInfo.put("status", vo.getStatus());
		modelInfo.put("statusName", vo.getStatusName());

		return modelAndView;
	}

	@RequestMapping("payOrder")
	public ModelAndView payRedirect(String tradeNo) throws Exception {
		ModelAndView modelAndView = new ModelAndView("/pay/error");
		Map<String, String> modelInfo = new HashMap<String, String>();
		PaymentVo paymentInfo = payService.queryPayment(tradeNo);
		PayChannelEnum payChannel = PayChannelEnum.fromCode(paymentInfo.getPayChannel());

		if(StringUtils.isNotBlank(tradeNo)){
			modelAndView = new ModelAndView("/pay/payRedirect", "model", modelInfo);
			modelInfo.put("orderId", paymentInfo.getOrderId());

			if(PayChannelEnum.WEIXIN.equals(payChannel)){
				modelAndView.setViewName("/pay/paymentWechat");
				modelInfo.put("tradeNo", paymentInfo.getTradeNo());
			}
			else if(PayChannelEnum.ZFB.equals(payChannel)){
				modelInfo.put("payForm", paymentInfo.getPayInfo());
			}
			else if(PayChannelEnum.WANGYIN.equals(payChannel)){
				modelInfo.put("payForm", paymentInfo.getPayInfo());
			}
			else if(PayChannelEnum.HUIKUAN.equals(payChannel)){
				String token = request.getParameterValues("token")[0];
				modelAndView = new ModelAndView("redirect:/order/info?token=" + token + "&orderId=" + paymentInfo.getOrderId());
			}
		}
		return modelAndView;
	}


	@RequestMapping(value = "updateCert")
	public Object updatePaymentCert(String orderId, String imgUrl,MultipartFile file) throws IOException {
        BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Integer resultCode = 1;
		Object result = null;
		//获取租户id
		String tenantId = TenantContext.getTenantId();
		if(StringUtils.isBlank(orderId)){
			resultCode = 0;
			errMsg = BusinessExceptionStatusEnum.updatePaymentCertRrror;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), result);
		}
		//获取原图片
		boolean success = false;
		OrderVo orderVo = orderService.getOrderById(orderId);
        if(orderVo != null){
            String imgUrl11 = orderVo.getCertImageUrl();
        	if(StringUtils.isNotBlank(imgUrl11)){
				List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(imgUrl11,tenantId);
				if(sysAttachments != null) {
					boolean b = FileUploadUtil.deleteFile(sysAttachments);
					if(b){
						consoleUserService.deleteImageUrl(imgUrl11);
                    }
				}else {
					success = true;
				}
			}else{
        		success = true;
			}
		}

		FastdfsVo fastdfsVo = new FastdfsVo();
	    if(success) {
			SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
			byte[] bytes = ResizeImage.resizeImageByte(file.getBytes(), 1);
			//获取租户上传的空间剩余大小
			String totalSpace = this.fileUploadService.selectTenantFileSize(tenantId, saasTenant.getBasePackageSpaceNum() + "");
			if (totalSpace != null) {
				//将当前文件大小转换成kb
				String uploadFileSize = FileSizeConversion.convertFileSize(Long.valueOf(bytes.length));
				if (Long.valueOf(uploadFileSize) > Long.valueOf(totalSpace)) {
					return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
				}


				//添加数据返回图片ID
				//获取用户信息
				TenantUserVo userInfo = TenantContext.getUserInfo();
				List<FastdfsVo> vos = FileUploadUtil.fileUpload(null,bytes,"png");
				FileVo vo = fileUploadService.insertFileUpload("3",userInfo.getUserId(),tenantId,saasTenant.getBasePackageSpaceNum(),"png",vos);
				if(vo != null){
                    //Map<String,String> imgMap = this.fileUploadCommonService.selectFileCommon(fileId);
                    Map map1 = new HashMap();
                    String primary = vo.getPrimary();
                    if(StringUtils.isNotBlank(primary)) {
                        map1.put("primary", primary);
                    }
					consoleUserService.saveImageUrl(userInfo.getUserId(),map1,vo.getId());
                }
				fastdfsVo.setId(vo.getId());
				fastdfsVo.setFileUrl(vo.getPrimary());
				try {
					payService.updatePaymentCertImg(orderId, vo.getId());
				} catch (Exception e) {
					resultCode = 0;
				}
			}else {
				return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
			}
		}else{
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.UploadResultError.getDescription());
		}
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(resultCode, errMsg.getDescription(), fastdfsVo);
	}


	@RequestMapping(value = "status")
	public ModelAndView payStatus(String orderId){
		PaymentVo vo = payService.getPaymentById(orderId);

		Map<String, Object> modelInfo = new HashMap<String, Object>();
		ModelAndView modelAndView = new ModelAndView("/pay/paymentState", "model", modelInfo);
		if(null != vo){
			modelInfo.put("orderId", orderId);
			modelInfo.put("channel", vo.getPayChannel());
			modelInfo.put("tradeNo", vo.getTradeNo());
			modelInfo.put("status", vo.getStatus());
			modelInfo.put("statusName", vo.getStatusName());
		}

		return modelAndView;
	}

}
