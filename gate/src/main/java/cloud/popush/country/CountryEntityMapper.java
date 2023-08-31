package cloud.popush.country;

import lombok.NonNull;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CountryEntityMapper {
    @Insert("""
            INSERT INTO `country`
            (
                `name`
            )
            VALUES
            (
                #{name}
            )
            """)
    Integer insert(@NonNull CountryEntity countryEntity);

    @Results(value = {
            @Result(property = "id", column = "country_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            <script>
                SELECT *
                FROM `country`
                <where>
                    <if test="condition.id != null">
                        AND `country_id` = #{condition.id}
                    </if>
                    <if test="condition.name != null">
                        AND `name` = #{condition.name}
                    </if>
                </where>
                LIMIT 100
            </script>
            """)
    List<CountryEntityReadonly> select(@Param("condition") @NonNull CountryEntityCondition condition);


    @Select("SELECT EXISTS(SELECT 1 FROM country WHERE `name`=#{name})")
    boolean exist(@Param("name") @NonNull String name);

    @Delete("""
            <script>
            DELETE FROM `country`
            <where>
                country_id = #{id}
            </where>
            </script>
            """)
    void delete(@NonNull Long id);
}
