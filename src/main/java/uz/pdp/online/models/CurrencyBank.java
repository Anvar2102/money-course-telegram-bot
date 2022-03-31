package uz.pdp.online.models;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "chBuilder")
public class CurrencyBank implements Serializable {
    private String CcyNm_EN;
    private String CcyNm_UZC;
    private String Diff;
    private Double Rate;
    private String Ccy;
    private String CcyNm_RU;
    private Integer id;
    private String CcyNm_UZ;
    private String Code;
    private String Nominal;
    private String Date;
}
