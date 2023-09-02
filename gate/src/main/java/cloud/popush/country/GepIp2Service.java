package cloud.popush.country;

import cloud.popush.cache.CacheExpiring;
import cloud.popush.exception.ArgumentException;
import cloud.popush.exception.MachineException;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AsnResponse;
import lombok.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class GepIp2Service {
    @Cacheable("GepIp2ServicegetAsnName")
    @CacheExpiring(600)
    public String getAsnName(@NonNull String ipStr) {
        File databaseFile;
        try {
            databaseFile = ResourceUtils.getFile("classpath:GeoLite2-ASN.mmdb");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        AsnResponse response;
        try (var reader = new DatabaseReader.Builder(databaseFile).withCache(new CHMCache()).build()) {
            response = reader.asn(InetAddress.getByName(ipStr));
        } catch (IOException | GeoIp2Exception e) {
            throw new RuntimeException(e);
        }

        return response.getAutonomousSystemOrganization();
    }

    @Cacheable("GepIp2ServicegetCountryName")
    @CacheExpiring(600)
    public String getCountryName(@NonNull String ipStr) throws ArgumentException, MachineException {
        File databaseFile;
        try {
            databaseFile = ResourceUtils.getFile("classpath:GeoLite2-Country.mmdb");
        } catch (FileNotFoundException e) {
            throw new MachineException("GeoLite2 Database is broken", e);
        }

        String response;
        try (var reader = new DatabaseReader.Builder(databaseFile).withCache(new CHMCache()).build()) {
            response = reader.country(InetAddress.getByName(ipStr)).getCountry().getName();
        } catch (GeoIp2Exception | UnknownHostException e) {
            return "Unknown";
        } catch (IOException e) {
            throw new MachineException("System error", e);
        }

        return response;
    }
}
