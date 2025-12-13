package com.TP_Metamapa.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, Object> attributes;

    public LocalDate getBirthdate() {
        if (attributes != null && attributes.containsKey("birthdate")) {
            Object birthdateObj = attributes.get("birthdate");
            if (birthdateObj instanceof List<?> list) {
                if (!list.isEmpty()) {
                    return LocalDate.parse(list.get(0).toString());
                }
            } else if (birthdateObj instanceof String) {
                return LocalDate.parse((String) birthdateObj);
            }
        }
        return null;
    }
}