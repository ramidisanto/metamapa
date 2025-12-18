package Servicio.Solicitudes;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
@Component
public class DetectorDeSpam {
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)\\b(https?://[^\\s<>\"']+|www\\.[^\\s<>\"']+|[a-z0-9.-]+\\.[a-z]{2,}(/[^\\s<>\"']*)?)",
            Pattern.CASE_INSENSITIVE
    );
    private static final String[] PALABRAS_SPAM = {
            "bitcoin", "btc", "crypto", "criptomoneda", "ethereum", "eth",
            "investment", "inversión", "forex", "trading", "ganar dinero",
            "earn money", "passive income", "ingresos pasivos", "loan", "préstamo",
            "wallet", "billetera", "binance", "coinbase", "nft",

            "buy now", "comprar ahora", "discount", "descuento", "offer", "oferta",
            "free", "gratis", "cheap", "barato", "best price", "mejor precio",
            "promo", "promoción", "gift card", "tarjeta de regalo", "giveaway", "sorteo",
            "winner", "ganador", "prize", "premio", "limited time", "tiempo limitado",


            "viagra", "cialis", "weight loss", "perder peso", "diet", "dieta",
            "pills", "pastillas", "pharmacy", "farmacia", "doctor", "treatment",
            "keta", "ketamina", "cbd", "thc",


            "casino", "poker", "bet", "apuesta", "slots", "tragamonedas",

            "click here", "clic aquí", "click below", "haz clic", "link in bio",
            "visit my", "visita mi", "check out", "mira esto", "subscribe", "suscríbete",
            "follow me", "sígueme", "sign up", "regístrate"
    };
    public boolean esSpam(String motivo) {
        if (motivo == null) return false;


        if (URL_PATTERN.matcher(motivo).find()) {
            return true;
        }
        String textoNormalizado = motivo.toLowerCase();

        textoNormalizado = textoNormalizado
                .replace("0", "o")
                .replace("1", "i")
                .replace("3", "e")
                .replace("@", "a")
                .replace("$", "s")
                .replaceAll("[^a-zñáéíóú ]", "");

        for (String palabra : PALABRAS_SPAM) {
            if (textoNormalizado.contains(palabra)) {
                return true;
            }
        }



        return false;
    }

}
