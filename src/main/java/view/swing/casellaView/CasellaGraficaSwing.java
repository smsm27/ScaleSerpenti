package view.swing.casellaView;

import model.casella.Casella;
import view.interfacce.casellaGrafica.CasellaGrafica;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;



public class CasellaGraficaSwing extends JPanel implements CasellaGrafica {
    protected Casella casella;
    protected ImageIcon immagine;
    protected JLabel numeroLabel; // Etichetta per il numero della casella
    protected Color c;

    public CasellaGraficaSwing(Casella casella, Color color) {
        System.out.println("creo casella "+ casella.getIndice());
        this.casella = casella;
        this.c= color;
        setBounds((int) casella.getPosizione().getX(), (int) casella.getPosizione().getY(), (int) casella.getLarghezza(), (int) casella.getAltezza());
        setLayout(null);
        // Aggiungi il numero come JLabel
        setNumeroLabel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (immagine != null) {
            g.drawImage(immagine.getImage(), 0, 0, (int)casella.getLarghezza(), (int)casella.getAltezza(), this);
        } else {
            g.setColor(c);
            g.fillRect(0, 0, (int)casella.getLarghezza(), (int)casella.getAltezza());
        }
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, (int)casella.getLarghezza() - 1, (int)casella.getAltezza() - 1);
    }

    @Override
    public void setNumeroLabel(){
        numeroLabel = new JLabel(String.valueOf(casella.getIndice()), SwingConstants.CENTER);
        numeroLabel.setBounds(0, 0, 20, 20); // Occupa tutta la casella
        numeroLabel.setForeground(Color.BLACK); // Colore del testo
        numeroLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Font personalizzato
        add(numeroLabel);
    }

    @Override
    public void aggiungiDragListener() {

    }

    @Override
    public Casella getCasella() {
        return casella;
    }

    @Override
    public void setImmagine() {
        // Ottieni l'URL dall'oggetto Casella
        String percorsoImmagine = casella.getImageURL();

        if (percorsoImmagine == null || percorsoImmagine.isEmpty()) {
            return; // Se non c'è URL, non fare nulla
        }

        try {
            // Controlla se il percorso è un URL o un percorso file locale
            if (percorsoImmagine.startsWith("http://") || percorsoImmagine.startsWith("https://") || percorsoImmagine.startsWith("file://")) {
                // È già un URL, usa URI e URL
                URI uri = new URI(percorsoImmagine);
                URL url = uri.toURL();
                immagine = new ImageIcon(url);
            } else {
                // È un percorso file locale, crea un oggetto File e ottieni l'URL
                File file = new File(percorsoImmagine);
                if (file.exists()) {
                    immagine = new ImageIcon(file.getAbsolutePath());
                } else {
                    System.err.println("File non trovato: " + percorsoImmagine);
                    immagine = null;
                }
            }

            // Ridimensiona l'immagine per adattarla alla casella
            if (immagine != null) {
                Image img = immagine.getImage();
                Image newImg = img.getScaledInstance((int)casella.getLarghezza(), (int)casella.getAltezza(), Image.SCALE_SMOOTH);
                immagine = new ImageIcon(newImg);
                repaint(); // Aggiorna il componente per mostrare la nuova immagine
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento dell'immagine: " + percorsoImmagine);
            immagine = null;
        }
    }

    @Override
    public void update() {
        repaint();
    }


}
