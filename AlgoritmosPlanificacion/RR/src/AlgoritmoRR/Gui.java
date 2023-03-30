package AlgoritmoRR;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.*;

import javax.swing.*;
import javax.swing.JFrame;

import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Gui extends JFrame {

    // Atributos
    private ArrayList<Nodo> list = new ArrayList<>();
    JTable tb1;
    Object datos[][];
    Object columnas[];
    int quantums;

    public Gui() {

        int Hl, Ta;
        ArrayList<Nodo> Temp = new ArrayList<>();
        quantums = Integer.parseInt(JOptionPane.showInputDialog("Ingresa el numero de quantums: "));
        
        int Ps = Integer.parseInt(JOptionPane.showInputDialog("cuantos procesos quiere?"));
        int NumPro = Ps;
 
        // Pedir datos
        for (int ct = Ps + 1; Ps != 0; Ps--) {
            String msg = "Hora de llegada P" + (ct - Ps);
            Hl = Integer.parseInt(JOptionPane.showInputDialog(msg));
            msg = "Tiempo de servicio P" + (ct - Ps);
            Ta = Integer.parseInt(JOptionPane.showInputDialog(msg));
            Nodo n = new Nodo(Hl, Ta);
            n.inicioBloqueo = Integer.parseInt(JOptionPane.showInputDialog("En que momento inicia el bloqueo?"));
            n.duracionBloqueo = Integer.parseInt(JOptionPane.showInputDialog("Cuanto dura el bloqueo?"));
            Temp.add(n);
        }

        // Organizar procesos SEGUN EL ORDEN DE LLEGADA
        boolean on = true;
        ArrayList<Nodo> FCFS0 = new ArrayList<>();
        ArrayList<Nodo> FCFS = new ArrayList<>();

        while (on) {
            int mini = 1000000;
            if (!Temp.isEmpty()) {
                int cont = 0, pos = 0;
                for (Nodo nodo : Temp) {
                    if (nodo.getHoraLlegada() <= mini) {
                        mini = nodo.getHoraLlegada();
                        pos = cont++;
                    } else {
                        cont++;
                    }
                }
                FCFS0.add(Temp.remove(pos));
            } else {
                on = false;
            }
        }

        on = true;
        while (on) {
            int mini = 1000000;
            if (!FCFS0.isEmpty()) {
                int cont = 0, pos = 0;
                for (Nodo nodo : FCFS0) {
                    if (nodo.getHoraLlegada() <= mini) {
                        mini = nodo.getHoraLlegada();
                        pos = cont++;
                    } else {
                        cont++;
                    }
                }
                FCFS.add(FCFS0.remove(pos));
            } else {
                on = false;
            }
        }

        // Guardar datos para medidas de pantalla y algoritmo
        int TimF = 0, num = 1;
        for (int cont = 0; cont < FCFS.size(); cont++) {
            FCFS.get(cont).setNumero_de_Procesos(num++);
            if (cont == 0) {
                TimF = FCFS.get(0).getHoraLlegada() + TimF + FCFS.get(cont).getTiempoEjecucion();
            } else {
                TimF = TimF + FCFS.get(cont).getTiempoEjecucion();
            }
            FCFS.get(cont).setT_Final(TimF);
            FCFS.get(cont).setT_Retorno(TimF - FCFS.get(cont).getHoraLlegada());
        }

        // Ajustar arraylists para las metricas
        FCFS0.clear();
        FCFS0.addAll(FCFS);
        for (Nodo n: FCFS){
            list.add(n.copiarNodo());
        }

        // Iniciar recoleccion de datos
        Color c;
        boolean despacho = false;
        int ultimaEjecucion = 0;
        int rafaga = 0;
        int ultE = 0;
        for (int i = 0; i < 40; i++) {
            boolean ejecutado = false, turno = false;
            // Caso del despachador
            if (despacho) {
                c = Color.BLUE;
                rafaga = 0;
                turno = true;
            } else if (i == 0) {
                c = Color.BLUE;
                turno = true;
            }
            despacho = false;
            // Caso del proceso
            for (Nodo nodo : FCFS0) {
                int posX = 50;
                if (nodo.getHoraLlegada() > i || nodo.estado == 4) {
                    continue;
                } else {
                    if (nodo.estado == 2 && nodo.getTiempoEjecucion()> 0) {
                        nodo.duracionBloqueo -= 1;
                    } else if (nodo.getTiempoEjecucion()> 0 && nodo.estado != 4) {
                        // Pausa de nodo debido a despachador
                        if (nodo.rafagaHecha && nodo.estado != 2 && nodo.getTiempoEjecucion()> 0 ){
                            c = Color.GRAY;
                            nodo.tiempoDeEspera += 1;
                            continue;
                        }
                        if (!turno) {
                            ejecutado = false;
                        } else {
                            ejecutado = true;
                        }
                        if (ejecutado) {
                            c = Color.GRAY;
                            nodo.tiempoDeEspera += 1;
                        } else {
                            c = Color.GREEN;
                            ultimaEjecucion = nodo.getNumero_de_Procesos();
                            turno = true;
                        }
                        if (c != Color.GRAY) {
                            nodo.setTiempoEjecucion(nodo.getTiempoEjecucion()- 1);
                            nodo.tiempoEjecucion += 1;
                            rafaga++;
                            ultE = nodo.getNumero_de_Procesos()- 1;
                        }
                        if (nodo.tiempoEjecucion == nodo.inicioBloqueo && nodo.duracionBloqueo != 0) {
                            nodo.estado = 2;
                            rafaga = quantums;
                            nodo.rafagaHecha = true;
                        }
                        if (nodo.getTiempoEjecucion()== 0) {
                            nodo.estado = 4;
                            nodo.tiempoFinalizacion = i+1;
                            rafaga = quantums;
                            nodo.rafagaHecha = true;
                        }
                        if (rafaga == quantums) {
                            despacho = true;
                            rafaga = 0;
                            nodo.rafagaHecha = true;
                        }
                    }
                }
            }
            for (Nodo nodo : FCFS0) {
                if (nodo.duracionBloqueo == 0 && nodo.estado == 2) {
                    nodo.estado = 3;
                }
            }
            // Borrar historial de rafagas
            if (FCFS0.get(ultimoNodoPosible(i,FCFS0)).rafagaHecha){
                for (Nodo n: FCFS0){
                    n.rafagaHecha = false;
                }
            }
        }
        
        // Comenzar personalizacion
        setTitle("Algoritmo RR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 450);

        // Contenido del JTable
        int Ncol = NumPro + 1;
        datos = new Object[9][Ncol];
        for (int fil = 0; fil < 9; fil++) {
            switch (fil) {
                case 0 -> datos[fil][0] = "Hora de llegada: ";
                case 1 -> datos[fil][0] = "Tiempo servicio: ";
                case 2 -> datos[fil][0] = "Inicio de bloqueo: ";
                case 3 -> datos[fil][0] = "Duración de bloqueo: ";
                case 4 -> datos[fil][0] = "Tiempo de espera: ";
                case 5 -> datos[fil][0] = "Tiempo de finalización: ";
                case 6 -> datos[fil][0] = "Tiempo de retorno: ";
                case 7 -> datos[fil][0] = "Tiempo perdido: ";
                case 8 -> datos[fil][0] = "Penalidad: ";
            }
            int con = 0;
            for (int col = 1; col < Ncol; col++, con++) {
                switch (fil) {
                    case 0 -> datos[fil][col] = list.get(con).getHoraLlegada();
                    case 1 -> datos[fil][col] = list.get(con).getHoraLlegada();
                    case 2 -> datos[fil][col] = list.get(con).inicioBloqueo;
                    case 3 -> datos[fil][col] = list.get(con).duracionBloqueo;
                    case 4 -> datos[fil][col] = FCFS0.get(con).tiempoDeEspera;
                    case 5 -> datos[fil][col] = FCFS0.get(con).tiempoFinalizacion;
                    case 6 -> datos[fil][col] = FCFS0.get(con).tiempoFinalizacion - FCFS.get(con).getHoraLlegada();
                    case 7 -> datos[fil][col] = FCFS0.get(con).tiempoFinalizacion - FCFS.get(con).getHoraLlegada() - list.get(con).getTiempoEjecucion();
                    case 8 -> datos[fil][col] = (FCFS0.get(con).tiempoFinalizacion - FCFS.get(con).getHoraLlegada())/ (float) list.get(con).getTiempoEjecucion();
                }
            }
        }

        int in = 0;
        Object columnas[] = new Object[Ncol];
        for (int i = 0; i < Ncol; i++) {
            if (i == 0) {
                columnas[i] = "Numero de proceso:";
            } else {
                columnas[i] = FCFS.get(in).getNumero_de_Procesos();
                in++;
            }
        }

        // Crear tabla
        tb1 = new JTable(datos, columnas);
        JScrollPane panel = new JScrollPane(tb1);

        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    public void paint(Graphics g) {
        super.paint(g);

        int sizelist = list.size();
        int size = list.get(sizelist - 1).getT_Final();


        // Dibujar tabla
        int total = 40 * 22; /////////////////////////////////////// Numero de columnas
        g.drawLine(50, 249, (total + 50), 249);
        sizelist += 1;
        sizelist *= 22;
        g.drawLine(50, 250, 50, (sizelist + 250));

        // Dibujar
        int lim = 21;
        for (int ps = 0; total > 0; total -= 17, ps++) {
            String proc = Integer.toString(ps);
            if (ps == 0) {
                g.drawString(proc, 50, 248);
            } else {
                g.drawString(proc, (lim + 50), 248);
                lim += 22;
            }
        }
        lim = 22;
        for (int ps = 0; sizelist > 0; sizelist -= 22, ps++) {
            String proc = Integer.toString(ps);
            if (ps == 0) {
                proc = "D";
                g.drawString(proc, 40, 271);
            } else {
                g.drawString(proc, 41, (lim + 271));
                lim += 22;
            }
        }

        // Establecer color para cada proceso
        for (Nodo nodo : list) {
           nodo.color = new Color(0, 143,57);
        }

        // Dibujar procesos
        int posY = 250, tfin = 0;
        int iniq, inip;

        boolean despacho = false;
        int ultimaEjecucion = 0;
        int rafaga = 0;
        
        int ultE = 0;
        
        for (int i = 0; i < 40; i++) {

            iniq = i * 22;
            boolean ejecutado = false, turno = false;

            // Impresion de despachador
            if (despacho) {
                g.setColor(Color.BLUE);
                g.fillRect((50 + iniq), (posY + 0), 20, 20);
                rafaga = 0;
                turno = true;
            } else if (i == 0) {
                iniq = 0;
                g.setColor(Color.BLUE);
                g.fillRect((50 + iniq), (posY + 0), 20, 20);
                turno = true;
            }

            despacho = false;
            

            // Impresion de procesos
            for (Nodo nodo : this.list) {
                if (nodo.getNumero_de_Procesos()== 1) {
                    inip = 22;
                } else {
                    inip = (nodo.getNumero_de_Procesos()) * 22;
                }

                int posX = 50;
                if (nodo.getHoraLlegada() > i || nodo.estado == 4) {
                    continue;
                } else {

                    if (nodo.estado == 2 && nodo.getTiempoEjecucion()> 0) {
                        nodo.duracionBloqueo -= 1;
                        g.setColor(Color.RED);
                        g.fillRect((posX + iniq), (posY + inip), 20, 20);

                    } else if (nodo.getTiempoEjecucion()> 0 && nodo.estado != 4) {
                    
                       
                        if (nodo.rafagaHecha && nodo.estado != 2 && nodo.getTiempoEjecucion()> 0 ){
                            g.setColor(Color.GRAY);
                            g.fillRect((posX + iniq), (posY + inip), 20, 20);
                            continue;
                        }
                        
                        if (!turno) {
                            ejecutado = false;
                        } else {
                            ejecutado = true;
                        }

                        if (ejecutado) {
                            g.setColor(Color.GRAY);
                        } else {
                            g.setColor(nodo.color);
                            ultimaEjecucion = nodo.getNumero_de_Procesos();
                            turno = true;
                        }

                        g.fillRect((posX + iniq), (posY + inip), 20, 20);
                        if (g.getColor() != Color.GRAY) {
                            nodo.setTiempoEjecucion(nodo.getTiempoEjecucion()- 1);
                            nodo.tiempoEjecucion += 1;
                            rafaga++;
                            ultE = nodo.getNumero_de_Procesos()- 1;
                        }

                        if (nodo.tiempoEjecucion == nodo.inicioBloqueo && nodo.duracionBloqueo != 0) {
                            nodo.estado = 2;
                            rafaga = quantums;
                            nodo.rafagaHecha = true;
                        }
                        if (nodo.getTiempoEjecucion()== 0) {
                            nodo.estado = 4;
                            rafaga = quantums;
                            nodo.rafagaHecha = true;
                        }
                        if (rafaga == quantums) {
                            despacho = true;
                            rafaga = 0;
                            nodo.rafagaHecha = true;
                        }
                    }
                }
            }

            for (Nodo nodo : list) {
                if (nodo.duracionBloqueo == 0 && nodo.estado == 2) {
                    nodo.estado = 3;
                }
            }
            
            // Borrar historial de rafagas
            if (list.get(ultimoNodoPosible(i,this.list)).rafagaHecha){
                for (Nodo n: list){
                    n.rafagaHecha = false;
                }
            }

            detenerElTiempo();

        }
        try {
            Thread.sleep(500000);
        } catch (Exception e) {
        }
    }

    public static void detenerElTiempo() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
        }
    }
    
    public int ultimoNodoPosible(int limite, ArrayList<Nodo> l){
        int indice = 0;
        for (Nodo n : l){
           
            if (n.getHoraLlegada() > limite){
                continue;
            } else {
                if (n.getTiempoEjecucion()> 0){
                    indice = n.getNumero_de_Procesos()-1;
                }
            }   
        }
        return indice;
    }
    
}
