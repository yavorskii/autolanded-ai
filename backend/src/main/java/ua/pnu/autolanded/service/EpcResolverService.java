package ua.pnu.autolanded.service;

import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EpcResolverService {

    public String resolveCatalogUrl(String make, String model, String vin) {
        String cleanMake = make != null ? make.trim().toLowerCase() : "";

        if ("tesla".equals(cleanMake)) {
            return "https://epc.tesla.com/";
        }

        if ("bmw".equals(cleanMake)) {
            if (vin != null && vin.length() >= 7) {
                String last7 = vin.substring(vin.length() - 7);
                return "https://www.realoem.com/bmw/enUS/select?vin=" + encode(last7);
            }
            return "https://www.realoem.com/bmw/enUS/select";
        }

        if (vin != null && vin.length() == 17) {
            return "https://partsouq.com/en/catalog/genuine/locate?c=" + encode(vin);
        }

        return "https://7zap.com/en/catalog/cars/" + encode(make);
    }

    public String buildAvtoProSearchUrl(String oemPartNumber, String partNameUa) {
        if (oemPartNumber != null && !oemPartNumber.isBlank()) {
            return "https://avto.pro/search/?code=" + encode(oemPartNumber);
        }
        return "https://avto.pro/search/?code=" + encode(partNameUa);
    }

    public String buildAllegroSearchUrl(String oemPartNumber, String partNameEn) {
        if (oemPartNumber != null && !oemPartNumber.isBlank()) {
            return "https://allegro.pl/listing?string=" + encode(oemPartNumber);
        }
        return "https://allegro.pl/listing?string=" + encode(partNameEn);
    }

    private String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
