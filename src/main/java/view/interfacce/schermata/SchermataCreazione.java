package view.interfacce.schermata;



public interface SchermataCreazione extends Schermata {
    void dialogCaricamento();
    void dialogSetNewTabella();
    void dialogAddElementoSpeciale(String tipo);
    void dialogGestisciSalvataggio();
    void rimuoviElementoGrafico(int indice);
    void pulisciVista();
}
