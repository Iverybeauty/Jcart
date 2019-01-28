package cn.wang.controller;

import cn.wang.dto.ProductAddDTO;
import cn.wang.dto.ProductListDTO;
import cn.wang.dto.ProductUpdateDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class ProductController {

    @RequestMapping("/add")
    public void add(@RequestBody ProductAddDTO productAddDTO){
        //同时操作 商品试题表和商品类目关系表
        //使用事务
    }
    @RequestMapping("/batchDelete")
    public void batchDelete(@RequestBody Long[] productIds){
    }

    @PostMapping("/update")
    public void update(@RequestBody ProductUpdateDTO productUpdateDTO){
    }

    @GetMapping("/searchProducts")
    public List<ProductListDTO> searchProducts(@RequestParam(required = false)String name,
                                                @RequestParam(required = false)Double price,
                                                @RequestParam(required = false,defaultValue = "1")Integer pageNum){
        return null;
    }


}
