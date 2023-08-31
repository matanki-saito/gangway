package cloud.popush.fingerprint;

import lombok.NonNull;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FingerprintEntityMapper {
    @Insert("""
            INSERT INTO `fingerprint`
            (
                `key`
            )
            VALUES
            (
                #{key}
            )
            """)
    Integer insert(@NonNull FingerprintEntity fingerprintEntity);

    @Results(value = {
            @Result(property = "id", column = "fingerprint_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("""
            <script>
                SELECT *
                FROM `fingerprint`
                <where>
                    <if test="condition.id != null">
                        AND `fingerprint_id` = #{condition.id}
                    </if>
                    <if test="condition.key != null">
                        AND `key` = #{condition.key}
                    </if>
                </where>
                LIMIT 100
            </script>
            """)
    List<FingerprintEntityReadonly> select(@Param("condition") @NonNull FingerprintEntityCondition fingerprintEntityCondition);

    @Select("SELECT EXISTS(SELECT 1 FROM `fingerprint` WHERE `key`=#{key})")
    boolean exist(@Param("key") @NonNull String key);

    @Delete("""
            <script>
            DELETE FROM `fingerprint`
            <where>
                fingerprint_id = #{id}
            </where>
            </script>
            """)
    void delete(@NonNull Long id);
}
