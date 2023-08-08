package cloud.popush.db;

import lombok.NonNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CountryEntityMapper {
    @Select("SELECT EXISTS(SELECT 1 FROM country WHERE `name`=#{name})")
    boolean exist(@Param("name") @NonNull String name);
}
