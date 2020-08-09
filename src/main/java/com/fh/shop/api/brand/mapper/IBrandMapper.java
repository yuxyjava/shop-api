package com.fh.shop.api.brand.mapper;

import com.fh.shop.api.brand.po.Brand;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IBrandMapper {

    @Insert("insert into t_brand (brandnameinfo) values (#{brandName})")
    public void addBrand(Brand brand);

    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "brandNameInfo", property = "brandName")
    })
    @Select("select id,brandNameInfo  from t_brand")
    List<Brand> findList();

    @Delete("delete from t_brand where id=#{v}")
    void delete(Long id);

    @Update("update t_brand set brandNameInfo=#{brandName} where id=#{id}")
    void update(Brand brand);


    void deleteBatch(List<Long> idList);
}
