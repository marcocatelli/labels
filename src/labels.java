import javax.swing.*;

/**
 * Created by mark on 28/02/17.
 */

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class labels {
    private JTextField txtMovimento;
    private JTextField txtDimDescr;
    private JCheckBox chkBordoInferiore;
    private JTextField txtOutputPath;
    private JButton btnCreaPdfDaCsv;
    private JButton btnSelezionaMovimento;
    private JButton btnSelezionaAnagrafica;
    private JTextField txtDimPrezzo;
    private JCheckBox chkPrezzoGrassetto;
    private JTextField txtAnagrafica;
    private JPanel panelMain;
    private JTextField txtMovSeparator;
    private JTextField txtMovNStartingChars;
    private JTextField txtMovNFinishingChars;
    private JTextField txtAnaSeparator;
    private JTextField txtAnaNStartingChars;
    private JTextField txtAnaNFinishingChars;
    private JTextField txtMovCodPosition;
    private JTextField txtAnaCodPosition;
    private JTextField txtAnaDescrPosition;
    private JTextField txtAnaPPosition;
    private JTextField txtMovQPosition;
    private JTextField txtEtichetteLibere;
    private JButton txtCreaDaExcelLibero;
    private File selectedFile;
    private Preferences pref;
    private final static float TOTAL_WIDTH = 105.0F;

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setContentPane(new labels().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public labels() {
        pref = Preferences.userRoot().node(this.getClass().getName());
        txtMovimento.setText(pref.get("FILEMOVIMENTO",""));
        txtAnagrafica.setText(pref.get("FILEARTICOLI", ""));
        txtOutputPath.setText(pref.get("OUTPUTPATH", ""));
        txtDimDescr.setText(pref.get("DIMDESCR", "11"));
        chkBordoInferiore.setSelected(pref.getBoolean("BORDOINFERIORE", false));
        txtDimPrezzo.setText(pref.get("DIMPREZZO", "16"));
        chkPrezzoGrassetto.setSelected(pref.getBoolean("PREZZOGRASSETTO", false));
        txtMovSeparator.setText(pref.get("MOVSEPARATOR",""));
        txtMovNStartingChars.setText(pref.get("MOVNSTARTINGCHARS",""));
        txtMovNFinishingChars.setText(pref.get("MOVNFINISHINGCHARS",""));
        txtMovCodPosition.setText(pref.get("MOVCODPOSITION",""));
        txtMovQPosition.setText(pref.get("MOVQPOSITION",""));
        txtAnaSeparator.setText(pref.get("ANASEPARATOR",""));
        txtAnaNStartingChars.setText(pref.get("ANANSTARTINGCHARS",""));
        txtAnaNFinishingChars.setText(pref.get("ANANFINISHINGCHARS","0"));
        txtAnaCodPosition.setText(pref.get("ANACODPOSITION",""));
        txtAnaDescrPosition.setText(pref.get("ANADESCRPOSITION",""));
        txtAnaPPosition.setText(pref.get("ANAPPOSITION",""));
        txtEtichetteLibere.setText(pref.get("FILEEXCELLIBERO",""));
        this.btnCreaPdfDaCsv.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    CreaDaCsv(txtMovimento.getText(), txtAnagrafica.getText());
                }
                catch (FileNotFoundException e1)
                {
                    e1.printStackTrace();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                catch (DocumentException e1)
                {
                    e1.printStackTrace();
                }
            }
        });
        txtCreaDaExcelLibero.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CreaDaExcelLibero();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (DocumentException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void SalvaPreferenze(){
        this.pref.put("OUTPUTPATH", this.txtOutputPath.getText());
        this.pref.put("FILEMOVIMENTO", this.txtMovimento.getText());
        this.pref.put("FILEARTICOLI", this.txtAnagrafica.getText());
        this.pref.put("DIMDESCR", this.txtDimDescr.getText());
        this.pref.put("DIMPREZZO", this.txtDimPrezzo.getText());
        this.pref.putBoolean("PREZZOGRASSETTO", this.chkPrezzoGrassetto.isSelected());
        this.pref.putBoolean("BORDOINFERIORE", this.chkBordoInferiore.isSelected());
        pref.put("MOVSEPARATOR",txtMovSeparator.getText());
        pref.put("MOVNSTARTINGCHARS",txtMovNStartingChars.getText());
        pref.put("MOVNFINISHINGCHARS",txtMovNFinishingChars.getText());
        pref.put("MOVCODPOSITION",txtMovCodPosition.getText());
        pref.put("MOVQPOSITION",txtMovQPosition.getText());
        pref.put("ANASEPARATOR",txtAnaSeparator.getText());
        pref.put("ANANSTARTINGCHARS",txtAnaNStartingChars.getText());
        pref.put("ANANFINISHINGCHARS",txtAnaNFinishingChars.getText());
        pref.put("ANACODPOSITION",txtAnaCodPosition.getText());
        pref.put("ANADESCRPOSITION",txtAnaDescrPosition.getText());
        pref.put("ANAPPOSITION",txtAnaPPosition.getText());
        pref.put("FILEEXCELLIBERO",txtEtichetteLibere.getText());
    }

    public void CreaDaExcelLibero() throws IOException, DocumentException {
        SalvaPreferenze();
        String outputPath = txtOutputPath.getText();
        int dimDescr = Integer.parseInt(txtDimDescr.getText());
        int dimPrezzo = Integer.parseInt(txtDimPrezzo.getText());
        boolean prezzoGrassetto = chkPrezzoGrassetto.isSelected();
        boolean bordoInferiore = chkBordoInferiore.isSelected();
        CSVReader reader = new CSVReader(new FileReader(txtEtichetteLibere.getText()),';');
        List<String[]>rows = reader.readAll();
        List<Etichetta>etichette = new ArrayList<Etichetta>();
        for (String[] row : rows){
            String descrizione = row[0];
            String prezzo = row[1];
            Etichetta etichetta = new Etichetta(descrizione,prezzo);
            etichette.add(etichetta);
        }
        if (etichette.size()==0){
            JOptionPane.showMessageDialog(null,"nessuna riga trovata","Messaggio",JOptionPane.OK_OPTION);
        }else{
            Font fontDescrizione = new Font();
            fontDescrizione.setFamily("Helvetica");
            fontDescrizione.setSize(dimDescr);
            Font fontPrezzo = new Font();
            fontPrezzo.setFamily("Helvetica");
            fontPrezzo.setSize(dimPrezzo);
            if (prezzoGrassetto)fontPrezzo.setStyle(Font.BOLD);
            Document document = new Document();
            document.setPageSize(new Rectangle(105.0F,70.0F));
            document.setMargins(0.0F,0.0F,0.0F,0.0F);
            PdfWriter writer= PdfWriter.getInstance(document,new FileOutputStream(outputPath));
            document.open();
            for (Etichetta etichetta : etichette){
                PdfPTable table = new PdfPTable(1);
                table.setTotalWidth(this.TOTAL_WIDTH);
                PdfPCell cell = new PdfPCell();
                cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                cell.setBorder(PdfPCell.NO_BORDER);
                Paragraph paragraph = new Paragraph(etichetta.getDescrizione(),fontDescrizione);
                cell.addElement(paragraph);
                table.addCell(cell);
                table.writeSelectedRows(0, -1, document.left(), document.top(), writer.getDirectContent());
                table = new PdfPTable(1);
                table.setTotalWidth(this.TOTAL_WIDTH);
                cell = new PdfPCell();
                cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                cell.setBorder(PdfPCell.NO_BORDER);
                paragraph = new Paragraph(etichetta.getPrezzoEuro(), fontPrezzo);
                cell.addElement(paragraph);
                if (bordoInferiore)cell.setBorder(PdfPCell.ALIGN_CENTER);
                table.addCell(cell);
                table.writeSelectedRows(0, -1, document.left(document.leftMargin()), table.getTotalHeight() + document.bottom(document.bottomMargin()), writer.getDirectContent());
                document.newPage();
            }
            document.close();
        }
    }

    public void CreaDaCsv(String fMov, String fAna)
            throws IOException, DocumentException
    {
        SalvaPreferenze();
        String outputPath = txtOutputPath.getText();
        int dimDescr = Integer.parseInt(txtDimDescr.getText());
        int dimPrezzo = Integer.parseInt(txtDimPrezzo.getText());
        boolean prezzoGrassetto = chkPrezzoGrassetto.isSelected();
        boolean bordoInferiore = chkBordoInferiore.isSelected();
        String movSeparator = txtMovSeparator.getText();
        int movNStartingChars = Integer.parseInt(txtMovNStartingChars.getText());
        int movNFinishingChars = Integer.parseInt(txtMovNFinishingChars.getText());
        int movCodPosition = Integer.parseInt(txtMovCodPosition.getText())-1;
        int movQPosition = Integer.parseInt(txtMovQPosition.getText())-1;
        String anaSeparator = txtAnaSeparator.getText();
        int anaNStartingChars = Integer.parseInt(txtAnaNStartingChars.getText());
        int anaNFinishingChars = Integer.parseInt(txtAnaNFinishingChars.getText());
        int anaCodPosition = Integer.parseInt(txtAnaCodPosition.getText())-1;
        int anaDescrPosition = Integer.parseInt(txtAnaDescrPosition.getText())-1;
        int anaPPosition = Integer.parseInt(txtAnaPPosition.getText())-1;

        CSVReader movReader = new CSVReader(new FileReader(fMov), ';');
        CSVReader anaReader = new CSVReader(new FileReader(fAna), ';');
        List<String[]>anaList = anaReader.readAll();
        List<String[]>movList = movReader.readAll();
        List<Etichetta>etichette = new ArrayList<Etichetta>();
        for (String[] movRow : movList){
            boolean trovato = false;
            String codiceArticoloMov = movRow[movCodPosition];
            for (String[] anaRow : anaList){
                String codiceArticoloAna = anaRow[anaCodPosition];
                if (codiceArticoloAna.equals(codiceArticoloMov)){
                    trovato = true;
                    String descrizione = anaRow[anaDescrPosition];
                    String prezzo = anaRow[anaPPosition];
                    int q = Integer.parseInt(movRow[movQPosition]);
                    for (int x = 0; x<q;x++){
                        Etichetta etichetta = new Etichetta (descrizione,prezzo);
                        etichette.add(etichetta);
                    }
                }
                if (trovato)break;
            }
        }
        if (etichette.size()==0){
            JOptionPane.showMessageDialog(null,"Nessuna etichetta creata","Messaggio",JOptionPane.OK_OPTION);
        } else {
            //BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
            Font fontDescrizione = new Font();
            fontDescrizione.setFamily("Helvetica");
            fontDescrizione.setSize(dimDescr);
            Font fontPrezzo = new Font();
            fontPrezzo.setFamily("Helvetica");
            fontPrezzo.setSize(dimPrezzo);
            if (prezzoGrassetto)fontPrezzo.setStyle(Font.BOLD);
            Document document = new Document();
            document.setPageSize(new Rectangle(105.0F,70.0F));
            document.setMargins(0.0F,0.0F,0.0F,0.0F);
            PdfWriter writer= PdfWriter.getInstance(document,new FileOutputStream(outputPath));
            document.open();
            for (Etichetta etichetta : etichette){
                PdfPTable table = new PdfPTable(1);
                table.setTotalWidth(this.TOTAL_WIDTH);
                PdfPCell cell = new PdfPCell();
                cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                cell.setBorder(PdfPCell.NO_BORDER);
                Paragraph paragraph = new Paragraph(etichetta.getDescrizione(),fontDescrizione);
                cell.addElement(paragraph);
                table.addCell(cell);
                table.writeSelectedRows(0, -1, document.left(), document.top(), writer.getDirectContent());
                table = new PdfPTable(1);
                table.setTotalWidth(this.TOTAL_WIDTH);
                cell = new PdfPCell();
                cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                cell.setBorder(PdfPCell.NO_BORDER);
                paragraph = new Paragraph(etichetta.getPrezzoEuro(), fontPrezzo);
                cell.addElement(paragraph);
                if (bordoInferiore)cell.setBorder(PdfPCell.ALIGN_CENTER);
                table.addCell(cell);
                table.writeSelectedRows(0, -1, document.left(document.leftMargin()), table.getTotalHeight() + document.bottom(document.bottomMargin()), writer.getDirectContent());
                document.newPage();
            }
            document.close();
        }
    }

}