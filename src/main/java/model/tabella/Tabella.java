package model.tabella;

import lombok.Getter;
import lombok.Setter;
import model.casella.Casella;

import java.util.List;

@Getter
@Setter
public class Tabella {
    private final int nElementiRiga=10;
    private List<Casella> caselle;
    private String imageURL;


}
