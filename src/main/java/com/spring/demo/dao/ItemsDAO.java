package com.spring.demo.dao;
/*
@author:zhengzhao
@time: 2018/08/20 
*/

import com.spring.demo.entity.Items;
import com.spring.demo.entity.Items;
import com.spring.demo.utils.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;


/**
 * 这里执行的是用户的业务逻辑，主要是控制显示在jsp页面上的数据
 */
public class ItemsDAO {
    /**
     * @return 所有的商品数据集
     */
    public ArrayList<Items> getAllItem(){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = DBConn.getConn();
            String sql = "select * from items;"; // SQL语句
            stmt = conn.prepareStatement(sql);//预编译sql语句
            result = stmt.executeQuery();
            ArrayList<Items> list = new ArrayList<Items>();
            while (result.next()) {
                Items items = new Items();
                items.setId(result.getInt("id"));
                items.setPrice(result.getInt("price"));
                items.setNumber(result.getInt("number"));
                items.setName(result.getString("name"));
                items.setCity(result.getString("city"));
                items.setPicture(result.getString("picture"));
                list.add(items);
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            //释放数据集对象
            if (result != null) {
                try {
                    result.close();
                    result = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            //释放语句对象
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        }
    }

    public boolean insertGoods(String name, String city, Integer price, Integer number, String picture){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConn.getConn();
			String sql = MessageFormat.format("insert into items (name,city,price,number,picture) " +
                    "values({0},{1},{2},{3},{4})",name,city,price,number,picture); // SQL语句
            String sql1 ="insert into items (name,city,price,number,picture) values('"+name+"','"+city+"','"+price+"','"+number+"','"+picture+"')";
			stmt = conn.prepareStatement(sql1);
			stmt.executeUpdate();
			return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
		finally {
			// 释放语句对象
				try {
					stmt.close();
					stmt = null;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
    }

    public Items getItemsById(int id){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConn.getConn();
			String sql = "select * from items where id=?;"; // SQL语句
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Items item = new Items();
				item.setId(rs.getInt("id"));
				item.setName(rs.getString("name"));
				item.setCity(rs.getString("city"));
				item.setNumber(rs.getInt("number"));
				item.setPrice(rs.getInt("price"));
				item.setPicture(rs.getString("picture"));
				return item;
            }
            else {
                return null;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
		finally {
			// 释放数据集对象
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			// 释放语句对象
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}
    }
    //获取最近浏览的前五条商品信息
	public ArrayList<Items> getViewList(String list)
	{
		System.out.println("list:"+list);
		ArrayList<Items> itemlist = new ArrayList<Items>();
		int iCount=5; //每次返回前五条记录
		if(list!=null&&list.length()>0)
		{
		    String[] arr = list.split("#");
		    System.out.println("arr.length="+arr.length);
		    //如果商品记录大于等于5条
		    if(arr.length>=5)
		    {
		       for(int i=arr.length-1;i>=arr.length-iCount;i--)
		       {
		    	  itemlist.add(getItemsById(Integer.parseInt(arr[i])));
		       }
		    }
		    else
		    {
		    	for(int i=arr.length-1;i>=0;i--)
		    	{
		    		itemlist.add(getItemsById(Integer.parseInt(arr[i])));
		    	}
		    }
		    return itemlist;
		}
		else
		{
			return null;
		}

	}


    public static void main(String[] args){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = DBConn.getConn();
            String name = "1";
            String city = "1";
            String price = "1";
            String number = "1";
            String picture = "1";
//            boolean b = new ItemsDAO().insertGoods(name,city,price,number,picture);
            }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
