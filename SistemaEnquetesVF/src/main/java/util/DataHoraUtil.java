package util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public final class DataHoraUtil {

    private static final ZoneId FUSO_BAHIA = ZoneId.of("America/Bahia");
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_INPUT = DateTimeFormatter
            .ofPattern("dd/MM/uuuu", PT_BR)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter HORA_INPUT = DateTimeFormatter.ofPattern("HH:mm", PT_BR);
    private static final DateTimeFormatter DATA_HORA_EXIBICAO = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", PT_BR);
    private static final DateTimeFormatter DATA_EXIBICAO = DateTimeFormatter.ofPattern("dd/MM/yyyy", PT_BR);

    private DataHoraUtil() {
    }

    public static LocalDateTime agora() {
        return LocalDateTime.now(FUSO_BAHIA);
    }

    public static LocalDateTime parseDataHora(String data, String hora) {
        if (data == null || data.isBlank() || hora == null || hora.isBlank()) {
            throw new DateTimeParseException("Data e hora são obrigatórias.", String.valueOf(data) + " " + String.valueOf(hora), 0);
        }
        return LocalDateTime.parse(data.trim() + " " + hora.trim(),
                DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm", PT_BR).withResolverStyle(ResolverStyle.STRICT));
    }

    public static String formatarDataHora(LocalDateTime valor) {
        return valor == null ? "—" : valor.format(DATA_HORA_EXIBICAO);
    }

    public static String formatarData(LocalDateTime valor) {
        return valor == null ? "—" : valor.format(DATA_EXIBICAO);
    }

    public static String formatarDataInput(LocalDateTime valor) {
        return valor == null ? "" : valor.format(DATA_INPUT);
    }

    public static String formatarHoraInput(LocalDateTime valor) {
        return valor == null ? "" : valor.format(HORA_INPUT);
    }
}
