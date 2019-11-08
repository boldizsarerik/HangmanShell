import com.sun.javafx.util.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shell.Command;
import shell.Shell;
import shell.Main;

public class hangman extends Shell{
    List<String> feladvanyok;
    int hibaszamlalo, j;
    boolean vanfeladvany; // van-e már választva feladvány a listából
    String feladvany;
    String[] megjelenites;
    String[] betuk = new String[100];
    
    protected void Init(){
        super.init();
        vanfeladvany = false;
        hibaszamlalo = 0;
        j = -1;
    }
    
    void megjelenites(){
        for(int i=0; i<megjelenites.length; ++i)
            {
                format("%s ", megjelenites[i]);
            }
            format("%n");
    }
    
    public hangman(){
        addCommand(new Command("load") {
            @Override
            public boolean execute(String... strings) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                if(strings.length != 1)
                    return false;
                else {
                    try { // fileból olvasó deklarálása
                        Scanner scfile = new Scanner(new File(strings[0]));
                        feladvanyok = new ArrayList<>();    
                        
                        while (scfile.hasNextLine()) {
                            feladvanyok.add(scfile.nextLine());
                        }
                        
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(hangman.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } // else vége
               
                return true;
            }
        });
        
        addCommand(new Command("new") {
            @Override
            public boolean execute(String... strings) {
                if(feladvanyok.size() == 0)
                    return false;
                Init();
                
                //Math.random(); // ad egy véletlenszerű double számot 0 és 1 között
                Random ran = new Random(); // Random(feladvanyok.size()); -- így is helyes,de nem tudom, mit csinál így a példányosításkor
                //ran.nextDouble(); // ez is ugyaanzt csinálja, mint a Math.random()
                int index = ran.nextInt(feladvanyok.size());
                feladvany = feladvanyok.get(index);
                vanfeladvany = true;
                
                megjelenites = new String[feladvany.length()];
                for(int i=0; i<feladvany.length(); ++i)
                {
                    megjelenites[i] = "_";
                }
                megjelenites();
                      
                return true;
            }
        });
        
        addCommand(new Command("print") {
            @Override
            public boolean execute(String... strings) {
                if(feladvanyok.isEmpty() || vanfeladvany == false)
                    return false;
                
                try {
                    format("%s%n", getHangman(hibaszamlalo));
                } catch (PhaseNumberOutOfBoundsException ex) {
                    Logger.getLogger(hangman.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                megjelenites();
                
                //System.out.println(feladvany);
                return true;
            }
        });
        
        addCommand(new Command("letter") {
            @Override
            public boolean execute(String... strings) { 
                String karakter = strings[0].toUpperCase();
                betuk[++j] = karakter;
                
                for(int i=0; i<j; ++i)
                    if(betuk[i].compareTo(karakter) == 0)
                        return false;
                
                if( feladvanyok.isEmpty() || vanfeladvany == false || strings.length != 1 || hibaszamlalo == 5 
                        || strings[0].length() != 1 || !Character.isLetter(karakter.charAt(0)) ) 
                    return false;
                              
                if(Utils.contains(feladvany, karakter)) { // a feladvany sztring tartalmazza-e a karakter sztringet
                    for(int i=0; i<feladvany.length(); ++i)
                        if(feladvany.charAt(i) == karakter.charAt(0))
                            megjelenites[i] = karakter;
                    
                    megjelenites();
                }
                else {
                    ++hibaszamlalo;
                    megjelenites();
                    if(hibaszamlalo == 5)
                        format("Ön 5x hibázott. Nem sikerült kitalálnia a feladványt, válasszon egy új feladványt!%n");
                }
                
                boolean kitalalta = true;
                for(int i=0; i<megjelenites.length; ++i)
                    if (megjelenites[i].compareTo("_") == 0)  // stringek összehasonlítása (ha megegyeznek)
                        kitalalta = false; 
                
                if(kitalalta == true)
                    format("Gratulálok! Megfejtette a feladványt! Válasszon egy új feladványt!%n");
                    
                return true;
            }
        });
    }
    
    public static void main(String[] args){
        Shell sh = Loader.load();
        sh.readEvalPrint();   
    }
}