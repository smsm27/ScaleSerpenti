package tools;

import lombok.Getter;
import lombok.Setter;
import model.casella.Casella;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TabellaStato implements Serializable {
    private List<Casella> caselle=new ArrayList<>();
}