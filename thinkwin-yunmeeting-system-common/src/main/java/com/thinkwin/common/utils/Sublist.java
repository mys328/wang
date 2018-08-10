package com.thinkwin.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/28 0028.
 */
public class Sublist {

  /*  public static void main(String[] args) {

        List<Integer> list=new ArrayList<Integer>();
        for(int i=1;i<52;i++){
            list.add(i);
        }

        paging(list,10);
        for(int i=0;i<list.size();i++){
            System.out.println(i);
        }

    }
*/

    /**
     * 一次性分页
     * @param list
     * @param pagesize
     */
    public static  void paging(List list,int pagesize){

        int totalcount=list.size();
        int pagecount=0;
        int m=totalcount%pagesize;
        if  (m>0){
            pagecount=totalcount/pagesize+1;
        }else{
            pagecount=totalcount/pagesize;
        }

        for(int i=1;i<=pagecount;i++){
            if (m==0){
                List<Integer> subList= list.subList((i-1)*pagesize,pagesize*(i));
                System.out.println(subList);
            }else{
                if (i==pagecount){
                    List<Integer> subList= list.subList((i-1)*pagesize,totalcount);
                    System.out.println(subList);
                }else{
                    List<Integer> subList= list.subList((i-1)*pagesize,pagesize*(i));
                    System.out.println(subList);
                }


            }
        }

    }



    public static void main(String[] args) throws Exception {
        List<Integer> p = new ArrayList<Integer>();

        List<Object> result = page(1,15,p);
        for(int i = 0; i < result.size(); i ++){
            System.out.println(result.get(i));
        }
    }
    /**
     *
     * @param pageNo 当前页码
     * @param pageSize 页数
     * @param list  所有集合
     * @return
     * @throws Exception
     */
    public static List page(int pageNo,int pageSize,List list){
        List<Object> result = new ArrayList<Object>();
        if(list != null && list.size() > 0){
            int allCount = list.size();
            int pageCount = (allCount + pageSize-1) / pageSize;
            if(pageNo >= pageCount){
                pageNo = pageCount;
            }
            int start = (pageNo-1) * pageSize;
            int end = pageNo * pageSize;
            if(end >= allCount){
                end = allCount;
            }
            for(int i = start; i < end; i ++){
                result.add(list.get(i));
            }
        }
        return result;
    }





}
