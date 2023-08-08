package cloud.popush.ip;

import lombok.NonNull;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface IpEntityMapper {
    @Insert("""
            INSERT INTO `ip`
            (
                `string`
            )
            VALUES
            (
                #{string}
            )
            """)
    Integer insert(@NonNull IpEntity ipEntity);

    @Results(value = {
            @Result(property = "id", column = "ip_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            <script>
                SELECT *
                FROM `ip`
                <where>
                    <if test="condition.id != null">
                        AND `fingerprint_id` = #{condition.id}
                    </if>
                    <if test="condition.string != null">
                        AND `string` = #{condition.string}
                    </if>
                </where>
                LIMIT 100
            </script>
            """)
    List<IpEntityReadonly> select(@Param("condition") @NonNull IpEntityCondition ipEntityCondition);

    @Select("SELECT EXISTS(SELECT 1 FROM `ip` WHERE `string`=#{string})")
    boolean exist(@Param("string") @NonNull String string);

    @Delete("""
            <script>
            DELETE FROM `ip`
            <where>
                ip_id = #{id}
            </where>
            </script>
            """)
    void delete(@NonNull Long id);
}
