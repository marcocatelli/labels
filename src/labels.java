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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
    private File selectedFile;
    private Preferences pref;

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
    }

    public void CreaDaCsv(String fMov, String fAna)
            throws IOException, DocumentException
    {
        this.pref.put("OUTPUTPATH", this.txtOutputPath.getText());
        this.pref.put("FILEMOVIMENTO", this.txtMovimento.getText());
        this.pref.put("FILEARTICOLI", this.txtAnagrafica.getText());
        this.pref.put("DIMDESCR", this.txtDimDescr.getText());
        this.pref.put("DIMPREZZO", this.txtDimPrezzo.getText());
        this.pref.putBoolean("PREZZOGRASSETTO", this.chkPrezzoGrassetto.isSelected());
        this.pref.putBoolean("BORDOINFERIORE", this.chkBordoInferiore.isSelected());
        CSVReader movReader = new CSVReader(new FileReader(fMov), ';');
        CSVReader anaReader = new CSVReader(new FileReader(fAna), ';');
        List ana = anaReader.readAll();
        String[] row = null;
        String[] anaRow = null;
        Document document = new Document();
        document.setPageSize(new Rectangle(105.0F, 70.0F));
        document.setMargins(0.0F, 0.0F, 0.0F, 0.0F);
        String fileName = this.txtOutputPath.getText();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();
        Font f = new Font();
        f.setFamily("Helvetica");
        f.setSize(Integer.parseInt(this.txtDimDescr.getText()));
        Font fPrezzo = new Font();
        fPrezzo.setFamily("Helvetica");
        fPrezzo.setSize(Integer.parseInt(this.txtDimPrezzo.getText()));
        if (this.chkPrezzoGrassetto.isSelected()) {
            fPrezzo.setStyle(1);
        }
        String codiceMov;
        while ((row = movReader.readNext()) != null)
        {
            codiceMov = row[9];
            for (Object object : ana)
            {
                anaRow = (String[])object;
                //String codiceAna = anaRow[1].substring(1, anaRow[1].length() - 1);
                String codiceAna = anaRow[1];
                if (codiceAna.equals(codiceMov))
                {
                    int quantità = Integer.parseInt(row[15]);
                    for (int i = 0; i < quantità; i++)
                    {
                        PdfPTable table = new PdfPTable(1);
                        table.setTotalWidth(105.0F);
                        PdfPCell cell = new PdfPCell();
                        cell.setVerticalAlignment(4);
                        cell.setBorder(0);
                        //String descrizione = anaRow[2].substring(1, anaRow[2].length() - 1);
                        String descrizione = anaRow[2];
                        Paragraph p = new Paragraph(descrizione, f);
                        cell.addElement(p);
                        table.addCell(cell);
                        table.writeSelectedRows(0, -1, document.left(), document.top(), writer.getDirectContent());

                        table = new PdfPTable(1);
                        table.setTotalWidth(105.0F);
                        cell = new PdfPCell();
                        cell.setVerticalAlignment(4);
                        cell.setBorder(0);
                        //String prezzo = "€ " + anaRow[12].substring(1, anaRow[12].length() - 1);
                        String prezzo = "€ " + anaRow[12];
                        prezzo = prezzo.replace(".", ",");
                        p = new Paragraph(prezzo, fPrezzo);
                        cell.addElement(p);
                        if (this.chkBordoInferiore.isSelected()) {
                            cell.setBorder(1);
                        }
                        table.addCell(cell);
                        table.writeSelectedRows(0, -1, document.left(document.leftMargin()), table.getTotalHeight() + document.bottom(document.bottomMargin()), writer.getDirectContent());
                        document.newPage();
                    }
                }
            }
        }
        document.close();
    }

}