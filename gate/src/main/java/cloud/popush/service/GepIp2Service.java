package cloud.popush.service;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CountryResponse;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class GepIp2Service {
    public String getAsnName(@NonNull String ipStr) {
        File databaseFile;
        try {
            databaseFile = ResourceUtils.getFile("classpath:GeoLite2-ASN.mmdb");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        AsnResponse response;
        try (var reader = new DatabaseReader.Builder(databaseFile).withCache(new CHMCache()).build()) {
            response = reader.asn(str2IpObj(ipStr));
        } catch (IOException | GeoIp2Exception e) {
            throw new RuntimeException(e);
        }

        return response.getAutonomousSystemOrganization();
    }

    public String getCountryName(@NonNull String ipStr) {
        File databaseFile;
        try {
            databaseFile = ResourceUtils.getFile("classpath:GeoLite2-Country.mmdb");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        CountryResponse response;
        try (var reader = new DatabaseReader.Builder(databaseFile).withCache(new CHMCache()).build()) {
            response = reader.country(str2IpObj(ipStr));
        } catch (IOException | GeoIp2Exception e) {
            throw new RuntimeException(e);
        }

        return response.getCountry().getName();
    }

    private InetAddress str2IpObj(String ipStr) {
        InetAddress ipAddress;
        try {
            ipAddress = InetAddress.getByName(ipStr);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return ipAddress;
    }
}
