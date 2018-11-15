package com.spring.demo.cartController;
/*
@author:zhengzhao
@time: 2018/08/22 
*/

import com.spring.demo.dao.ItemsDAO;
import com.spring.demo.entity.Cart;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import com.spring.demo.entity.Items;
import java.io.File;
import java.io.IOException;
import java.util.*;


@Controller
@RequestMapping("/cart")
public class cartDemo {
    @Autowired
	private  HttpServletRequest request;

    @RequestMapping("")
    public String index(){
        return "index";
    }
    /**
     * 这里是处理返回指定商品页面的信息
     * @param id "商品ID"
     * @param model "需要返回的信息"
     * @return "成功或者错误的页面"
     */
    //这里将url请求改为REST风格
    @RequestMapping(value = "/details/{id}" ,method = RequestMethod.GET)
    public String details(@PathVariable("id") Integer id , Map model ){
        request.getSession().getAttribute("cart");
        if (id != null) {
            ItemsDAO itemsDAO= new ItemsDAO();
            Items items = itemsDAO.getItemsById(id);
            if (items != null) {
                model.put("id",items.getId());
                model.put("price",items.getPrice());
                model.put("num",items.getNumber());
                model.put("name",items.getName());
                model.put("city",items.getCity());
                model.put("picture",items.getPicture());
                return "details";
            }
            else {
                model.put("error","ID不存在");
                return "error";
            }
        }
        else {
            model.put("error","参数不全");
            return "error";
        }

    }

    /**
     * 这里主要是处理用户添加购物车的逻辑，此处本应该使用POST方法接受请求
     * 但是lhgdialog弹窗框架没有找到发起post请求的办法，被迫使用GET方法
     * @param id "商品的ID"
     * @param num "购买的数量"
     * @param modle "成功的信息"
     * @return "添加成功或错误的页面"
     */
    @RequestMapping(value = "/details/{id}/{num}",method = RequestMethod.GET)
    public String detailsAddInCart(@PathVariable("id") Integer id,@PathVariable("num") Integer num, Map modle){
        ItemsDAO itemsDAO = new ItemsDAO();
        Items items = itemsDAO.getItemsById(id);
        if (items != null) {
                if (request.getSession().getAttribute("cart") == null) {
                Cart cart = new Cart();
                request.getSession().setAttribute("cart",cart);
            }
            Cart cart =(Cart) request.getSession().getAttribute("cart");
            if(cart.addGoodsInCart(items,num)){
                modle.put("id",id);
                modle.put("num",num);
                return "success";
            }else {
                return "failure";
            }
        }else {
            return "failure";
        }

    }

    /**查看购物车
     * @return
     */
    @RequestMapping(value = "/show")
    public String show(){
        return "cart";
    }

    /**
     * 从购物车中删除商品
     * @return
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public String delInCart(@PathVariable("id") Integer id ,Map model){
        if (id != null) {
            if (request.getSession().getAttribute("cart") != null) {
                ItemsDAO itemsDAO = new ItemsDAO();
                Items idItems = itemsDAO.getItemsById(id);
                if (idItems != null) {
                    Cart cart =(Cart) request.getSession().getAttribute("cart");
                    HashMap<Items,Integer> goods = cart.getGoods();
                    Set<Items> items =goods.keySet();
                    Iterator<Items> item = items.iterator();
                    while (item.hasNext()){
                        Items it = item.next();
                        if (it.equals(idItems)){
                            if (cart.removeGoodsInCart(idItems)){
                                return "cart";
                            }else {
                                model.put("error","删除失败");
                                return "error";
                            }
                        }
                    }
                    return "cart";
                }else {
                    model.put("error","ID不存在");
                    return "error";
                }

            }else {
                model.put("error","购物车为空");
                return "error";
            }
        }else {
            model.put("error","参数不全");
            return "error";
        }
    }

    /**
     * 这里使用的是JSON的方式返回数据，主要实现了购物车中对商品购买数量的增加和减少，前端采用的是Ajax异步加载的方式
     * @param id "商品的id"
     * @param action "0/1"  0代表的减少数量    1代表是增加商品数量
     * @return  将成功或者失败的信息封装在json对象中，方便前端页面的读取
     */
    @RequestMapping(value = "/{id}/{action}",method = RequestMethod.PUT , produces = "application/json; charset=utf-8")
    @ResponseBody
    public String action (@PathVariable("id") Integer id , @PathVariable("action") Integer action){
        JSONObject jsonObject = new JSONObject();
        if (action.equals(0)){//0代表减少
            JSONObject data = addOrSub(id,"sub");
            if (data.get("error")==null){
                jsonObject.put("success",data);
                return jsonObject.toString();
            }else {
                jsonObject.put("error",data);
                return jsonObject.toString();
            }
        }else if (action.equals(1)){//代表增加
            JSONObject data = addOrSub(id,"add");
            if (data.get("error")==null){
                jsonObject.put("success",data);
                return jsonObject.toString();
            }else {
                jsonObject.put("error",data);
                return jsonObject.toString();
            }
        }else {
            JSONObject data = new JSONObject();
            data.put("error","操作不正确");
            data.put("code","500");
            jsonObject.put("error",data);
            return jsonObject.toString();
        }
    }

    /**
     * 增加和减少商品数量的接口，根据不同的参数执行不同的方法
     * @param id "商品的ID"
     * @param action "add/sub"
     * @return "将成功或者失败的信息封装在json对象中，方便前端对数据的读取"
     */
    public JSONObject addOrSub(Integer id , String action){
        JSONObject data = new JSONObject();
        ItemsDAO itemsDAO = new ItemsDAO();
        Items idItems = itemsDAO.getItemsById(id);
        if (idItems != null) {
            if (request.getSession().getAttribute("cart") != null) {
                Cart cart = (Cart) request.getSession().getAttribute("cart");
                HashMap<Items,Integer> goods= cart.getGoods();
                Set<Items> items = goods.keySet();
                Iterator<Items> itemsIterator =  items.iterator();
                while (itemsIterator.hasNext()){
                    Items item = itemsIterator.next();
                    if (item.equals(idItems)){
                        if(action.equals("add")){
                            goods.put(idItems,goods.get(idItems)+1);//更新购物车内的商品数量
                            data.put("num",goods.get(idItems));
                            data.put("allPrice",goods.get(idItems)*item.getPrice());
                            data.put("calTotalPrice",cart.calTotalPrice());
                            return data;
                        }else {
                            goods.put(idItems,goods.get(idItems)-1);
                            data.put("num",goods.get(idItems));
                            data.put("allPrice",goods.get(idItems)*item.getPrice());
                            data.put("calTotalPrice",cart.calTotalPrice());
                            return data;
                        }
                    }
                }
                data.put("error","该商品不在购物车中");
                return data;
            }else {
                data.put("error","购物车为空");
                return data;
            }
        }else {
            data.put("error","ID不存在");
            return data;
        }
    }

    /**跳转到添加商品的页面
     * @return
     */
    @RequestMapping(value = ("/add") ,method = RequestMethod.GET)
    public String addPage(){
        return "addGood";
    }

    /**
     * @param name “商品名”
     * @param city “商品产地”
     * @param price "商品单价"
     * @param number "商品库存"
     * @param picture "商品图片"
     * @return "是否成功"
     * @throws IOException
     */
    @RequestMapping(value = ("/add") ,method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    @ResponseBody
    public String addGood(@RequestParam("name")String name,@RequestParam("city")String city,
                          @RequestParam("price")Integer price,@RequestParam("number")Integer number,
            @RequestParam("picture")MultipartFile picture) throws IOException {
        JSONObject jsonObject =new JSONObject();
        if(!picture.isEmpty()){
            String fileName = System.currentTimeMillis()+ picture.getOriginalFilename();//在文件名前加上时间戳
            String pathVal = request.getSession().getServletContext().getRealPath("/");
            FileUtils.copyInputStreamToFile(picture.getInputStream(),
                    new File(pathVal+"resources\\images\\",
                            fileName));
            ItemsDAO itemsDAO = new ItemsDAO();
            if (itemsDAO.insertGoods(name,city,price,number,fileName)){
                jsonObject.put("success","ok");
                return jsonObject.toString();
            }
        }
        jsonObject.put("error","出错了");
        return jsonObject.toString();
    }
}
