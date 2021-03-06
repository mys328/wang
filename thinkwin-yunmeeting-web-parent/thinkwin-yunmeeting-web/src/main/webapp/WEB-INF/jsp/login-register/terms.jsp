<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2017/5/28
  Time: 14:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>企云会-连接组织的智慧</title>
    <%@ include file="/WEB-INF/jsp/common/head.jsp"%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/login-register-common.css"><%--登陆注册公共样式--%>
    <link rel="stylesheet" href="<%=basePath%>static/css/login-register/terms.css">
</head>
<body>
    <!-- 服务条款 -->
    <div class="serve-terms">
        <%@ include file="/WEB-INF/jsp/common/login-logo.jsp"%>
        <%--<p style="width: 100%;text-align: center;">服务条款</p>--%>
        <div class="termsBox">
        <h1 class="center">企云会服务协议</h1>
        <div>提示：在使用服务之前，您应当认真阅读并遵守《企云会服务协议》（以下简称“本协议”），请您务必审慎阅读、充分理解各条款内容。</div>
        <div>当您按照注册页面提示填写信息、阅读并同意本协议且完成全部注册程序后，或您按照激活页面提示填写信息、阅读并同意本协议且完成全部激活程序后，或您以其他允许的方式实际使用本服务时，即表示您已充分阅读、理解并接受本协议的全部内容，本协议即产生法律约束力。您承诺接受并遵守本协议的约定，届时您不应以未阅读本协议的内容等理由，主张本协议无效，或要求撤销本协议。</div>
        <h5>一、合约主体</h5>
        <div>本合约是企云会用户(包括个人、企业及其他组织)与企云会运营企业北京盛科维科技发展有限公司（以下简称“盛科维”）之间的法律契约。</div>
        <h5>二、协议内容和效力</h5>
        <div>1、本协议内容包括本协议正文及所有企云会已经发布或将来可能发布的隐私政策、各项政策、规则、声明、通知、警示、提示、说明（以下简称“规则”）。前述规则为本协议不可分割的组成部分，与协议正文具有同等法律效力。
        </div>
        <div>2、企云会有权根据需要不时制订、修改本协议及相关规则，变更后的协议和规则一经公布，立即取代原协议及规则并自动生效。如您不同意相关变更，应当立即停止使用企云会服务，如您继续使用企云会服务或进行任何企云会活动，即表示您已接受经修订的协议和规则。
        </div>
        <h5>三、服务内容</h5>
        <div>1、企云会提供手机微信端、电脑WEB端等各种形式服务软件（具体以盛科维提供的为准），盛科维会不断丰富您使用本服务的终端、形式等，如您已注册使用一种形式的服务，则可以以同一账号使用其他版本服务，本协议自动适用于您对所有版本的软件和服务的使用。
        </div>
        <div>2、本服务内容包含会议预订、会议室管理、会议预订授权、会议通知等技术功能，这些功能服务可能根据用户需求的变化，随着因服务版本不同、或服务提供方的单方判断而被优化或修改，或因定期、不定期的维护而暂缓提供。
        </div>
        <div>3. 企云会保留在任何时候自行决定对服务或服务任何部分及其相关功能、应用软件变更、升级、修改、转移的权利。您同意，对于上述行为，企云会均不需通知，并且对您和任何第三方不承担任何责任。
        </div>
        <div>
            4.您可以注册企业服务管理账号，通过该账号上传和管理企业成员通讯录，邀请企业成员加入，并通过企云会服务实现企业会议科学管理、提高工作效率等目的。
        </div>
        <h5>四、用户注册及账号管理</h5>
        <div>1、在您完成注册程序或以其他企云会允许的方式实际使用服务时，您应当是具备完全民事权利能力和与所从事的民事行为相适应的行为能力的自然人、法人或其他组织。若您不具备前述主体资格，请勿使用服务，否则您及您的监护人应承担因此而导致的一切后果，且企云会有权注销（永久冻结）您的账户，并向您及您的监护人索偿。如您代表一家公司或其他法律主体进行注册或以其他企云会允许的方式实际使用本服务，则您声明和保证，您有权使该公司或该法律主体受本协议“条款“的约束。</div>
        <div>
            2、您可以使用您提供或确认的手机号或者企云会允许的其它方式作为账号进行注册，并承诺注册的账号名称、头像和简介等信息中不得出现违法和不良信息，不得冒充他人进行注册，不得未经许可为他人注册，不得以可能导致其他用户误认的方式注册账号，不得使用可能侵犯他人权益的用户名进行注册（包括但不限于涉嫌商标权、名誉权侵权等），否则企云会有权不予注册或停止服务并收回账号，因此产生的损失由您自行承担。
        </div>
        <div>
            3、您了解并同意，企云会注册账号（包括客户端账号及服务管理账号）所有权归属于盛科维，注册完成后，您仅获得账号使用权。企云会账号使用权仅归属于初始申请注册人。
        </div>
        <div>
            4、在您成功注册后，企云会将根据账号和密码确认您的身份。您应妥善保管您的账户和密码，并对利用该账户和密码所进行的一切活动（包括但不限于网上点击同意或提交各类规则协议或购买服务、会议室预订、单位人员添加删除等）负全部责任。您承诺，在密码或账户遭到未获授权的使用，或者发生其他任何安全问题时，将立即通知企云会，且您同意并确认，企云会不对上述情形产生的任何直接或间接的遗失或损害承担责任。
        </div>
        <h5>五、服务使用规范</h5>
        <div>
            1、用户充分了解并同意，企云会仅为用户提供服务平台，您应自行对利用服务从事的所有行为及结果承担责任。相应地，您应了解，使用企云会服务可能发生来自他人非法或不当行为（或信息）的风险，您应自行判断及行动，并自行承担相应的风险。
        </div>
        <div>
            2、您应自行承担因使用服务所产生的费用，包括但不限于上网流量费、通信服务费等。
        </div>
        <div>
            3、除非另有说明，本协议项下的服务只能用于非商业用途。您承诺不对本服务任何部分或本服务之使用或获得，进行复制、拷贝、出售、转售或用于包括但不限于广告及任何其它商业目的。
        </div>
        <div>
            4、您在使用本服务过程中，本服务条款内容、页面上出现的关于交易操作的提示或企云会发送到您手机的信息（短信或电话等）内容是您使用本服务的相关规则，您使用本服务即表示您同意接受本服务的相关规则。您了解并同意企云会有权单方修改服务的相关规则，而无须征得您的同意，服务规则应以您使用服务时的页面提示（或发送到该手机的短信或电话等）为准，您同意并遵照服务规则是您使用本服务的前提。
        </div>
        <div>
            5、企云会可能会以电子邮件（或发送到您手机的短信或电话等）方式通知您服务进展情况以及提示您进行下一步的操作，但企云会不保证您能够收到或者及时收到该邮件（或发送到该手机的短信或电话等），且不对此承担任何后果。因此，在服务过程中您应当及时登录到本网站查看和进行交易操作。因您没有及时查看和对服务状态进行修改或确认或未能提交相关申请而导致的任何纠纷或损失，企云会不负任何责任。
        </div>
        <div>
            6、如果您通过使用本服务，将获取使用来自第三方的任何产品或服务，您还可能受限于该第三方的相关条款和条件，服务的内容和结果由该第三方提供、企云会对此不予过问亦不承担任何责任，企云会仅作为平台提供者，本服务条款不影响您与该第三方的法律关系。
        </div>
        <div>
            7、您确认并同意，企云会的各关联公司均为本服务条款的第三方受益人，且企云会关联公司有权直接强制执行并依赖本服务条款中授予其利益的任何规定。除此之外，任何第三方均不得作为本服务条款的第三方受益人。
        </div>
        <h5>六、您的权利和义务</h5>
        <div>
            1、您有权利享受企云会提供的互联网技术和信息服务，并有权利在接受企云会提供的服务时获得企云会的技术支持、咨询等服务，服务内容详见本网站相关产品介绍。
        </div>
        <div>
            2、您应尊重企云会及其他第三方的知识产权和其他合法权利，并保证在发生侵犯上述权益的违法事件时尽力保护企云会及其股东、雇员、合作伙伴等免于因该等事件受到影响或损失；企云会保留您侵犯企云会合法权益时终止向您提供服务并不退还任何款项的权利。
        </div>
        <div>
            3、对由于您向企云会提供的联络方式有误以及您用于接受企云会邮件的电子邮箱安全性、稳定性不佳而导致的一切后果，您应自行承担责任，包括但不限于因您未能及时收到企云会的相关通知而导致的后果和损失。
        </div>
        <div>
            4、您使用企云会产品或服务时将遵从国家、地方法律法规、行业惯例和社会公共道德，不会利用企云会提供的服务进行存储、发布、传播如下信息和内容：违反国家法律法规政策的任何内容（信息）；违反国家规定的政治宣传和/或新闻信息；涉及国家秘密和/或安全的信息；封建迷信和/或淫秽、色情、下流的信息或教唆犯罪的信息；博彩有奖、赌博游戏；违反国家民族和宗教政策的信息；妨碍互联网运行安全的信息；侵害他人合法权益的信息和/或其他有损于社会秩序、社会治安、公共道德的信息或内容; 如您违反上述保证，企云会除有权根据相关服务条款采取删除信息、中止服务、终止服务的措施，并有权限制您账户如新购产品或服务、续费等部分或全部功能，如因您上述行为给企云会造成损失的，您应予赔偿。
        </div>
        <div>
            5、您充分了解并同意，您必须为自己注册的企业帐号下的一切行为负责，包括您所发表的任何内容以及由此产生的任何后果。您应对使用本服务时接触到的内容自行加以判断，并承担因使用内容而引起的所有风险，包括因对内容的正确性、完整性或实用性的依赖而产生的风险。企云会无法且不会对您因前述风险而导致的任何损失或损害承担责任。
        </div>
        <div>
            6、如果企云会发现或收到他人举报您有违反本协议约定的，企云会有权不经通知随时对相关内容进行删除、屏蔽，并采取包括但不限于收回账号，限制、暂停、终止您使用部分或全部本服务，追究法律责任等措施。
        </div>
        <h5>七、企云会的权利与义务</h5>
        <div>
            1、企云会应根据您选择的服务以及交纳款项的情况向您提供合格的网络技术和信息服务。
        </div>
        <div>
            2、企云会承诺对您资料采取对外保密措施，不向第三方披露您资料，不授权第三方使用您资料，除非：行政、司法等职权部门要求企云会提供；企云会解决举报事件、提起诉讼而提交的；企云会为防止严重违法行为或涉嫌犯罪行为发生而采取必要合理行动所必须提交的；企云会为向您提供产品、服务、信息而向第三方提供的，包括企云会通过第三方的技术及服务向您提供产品、服务、信息的情况。
        </div>
        <div>
            3、对于不按时交纳服务款项、违反国家法律和本协议禁止的行为，企云会有权终止提供服务。
        </div>
        <h5>八、知识产权</h5>
        <div>
            1、您了解及同意，除非企云会另行声明，本协议项下服务包含的所有产品、技术、软件、程序、数据及其他信息（包括但不限于文字、图像、图片、照片、音频、视频、图表、色彩、版面设计、电子文档）的所有知识产权（包括但不限于版权、商标权、专利权、商业秘密等）及相关权利均归企云会运营关联公司盛科维所有。
        </div>
        <div>
            2、您应保证，除非取得盛科维书面授权，对于上述权利您不得（并不得允许任何第三人）实施包括但不限于出租、出借、出售、散布、复制、修改、转载、汇编、发表、出版、还原工程、反向汇编、反向编译，或以其它方式发现原始码等的行为。
        </div>
        <div>
            3、企云会服务涉及的Logo、“企云会”、等文字、图形及其组成，以及企云会其他标识、徵记、产品和服务名称均为企云会及其运营公司盛科维在中国和其它国家的商标，用户未经盛科维书面授权不得以任何方式展示、使用或作其他处理，也不得向他人表明您有权展示、使用、或作其他处理。
        </div>
        <h5>九、隐私政策</h5>
        <div>
            1、您在企云会服务注册的账户具有密码保护功能，以确保您基本信息资料的安全，请您妥善保管账户及密码信息。
        </div>
        <div>
            2、企云会努力采取各种合理的物理、电子和管理方面的安全措施来保护您的信息，使您存储在企云会中的信息和内容不会被泄漏、毁损或者丢失，包括但不限于SSL、信息加密存储、数据中心的访问控制。我们对可能接触到信息的员工也采取了严格管理，包括但不限于根据岗位的不同采取不同的权限控制，与他们签署保密协议，监控他们的操作情况等措施。企云会会按现有技术提供相应的安全措施来保护您的信息，提供合理的安全保障，企云会将在任何时候尽力做到使您的信息不被泄漏、毁损或丢失， 但同时也请您注意在信息网络上不存在绝对完善的安全措施，请妥善保管好相关信息。
        </div>
        <div>
            3、企云会有权根据实际情况自行决定您在本软件及服务中数据的最长储存期限、服务器中数据的最大存储空间等，您应当保管好终端、账号及密码，并妥善保管相关信息和内容，企云会对您自身原因导致的数据丢失或被盗以及在本软件及服务中相关数据的删除或储存失败不承担责任。
        </div>
        <h5>十、争议解决及其他</h5>
        <div>
            1、本协议之解释与适用，以及与本协议有关的争议，均应依照中华人民共和国法律予以处理，并以北京市海淀区人民法院为第一审管辖法院。
        </div>
        <div>
            2、如本协议的任何条款被视作无效或无法执行，则上述条款可被分离，其余部份则仍具有法律效力。
        </div>
        <div>
            3、企云会于用户过失或违约时放弃本协议规定的权利的，不得视为其对用户的其他或以后同类之过失或违约行为弃权。
        </div>
        <div>
            4、本协议应取代双方此前就本协议任何事项达成的全部口头和书面协议、安排、谅解和通信。
        </div>
      </div>
    </div>
</body>
</html>
