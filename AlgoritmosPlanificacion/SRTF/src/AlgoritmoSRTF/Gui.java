package AlgoritmoSRTF;

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
    Object datos[][];


    public Gui() {

         int Hora_Llegada, Tiempo_Ejecucion;
        ArrayList<Nodo> procesos = new ArrayList<>();
        int cantidad_Procesos = Integer.parseInt(JOptionPane.showInputDialog("Inserte el número de procesos"));
        int Numero_de_Procesos = cantidad_Procesos;

        // Pedir datos al usuario
        for (int i = cantidad_Procesos + 1; cantidad_Procesos != 0; cantidad_Procesos--) {
            String msg = "Hora de llegada P" + (i - cantidad_Procesos);
            Hora_Llegada = Integer.parseInt(JOptionPane.showInputDialog(msg));
            msg = "Tiempo de ejecucion del proceso " + (i - cantidad_Procesos);
            Tiempo_Ejecucion = Integer.parseInt(JOptionPane.showInputDialog(msg));
            Nodo n = new Nodo(Hora_Llegada, Tiempo_Ejecucion);
            n.inicioBloqueo = Integer.parseInt(JOptionPane.showInputDialog("Inicio del bloque"));
            n.duracionBloqueo = Integer.parseInt(JOptionPane.showInputDialog("Duración del bloqueo"));
            procesos.add(n);
        }

        // Organizar procesos SEGUN EL ORDEN DE LLEGADA
        boolean on = true;
        ArrayList<Nodo> FCFS0 = new ArrayList<>();
        ArrayList<Nodo> FCFS = new ArrayList<>();

        while (on) {
            int mini = 1000000;
            if (!procesos.isEmpty()) {
                int cont = 0, pos = 0;
                for (Nodo nodo : procesos) {
                    if (nodo.getHoraLlegada() <= mini) {
                        mini = nodo.getHoraLlegada();
                        pos = cont++;
                    } else {
                        cont++;
                    }
                }
                FCFS0.add(procesos.remove(pos));
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

        // Guardar analitics
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
        
        // Toma de metricas
        int ultimaEjecucion = 0;
        Color c;
        for (int i = 0; i < 40; i++) {
            boolean ejecutado = false, turno = false;
            for (Nodo nodo : FCFS0) {
                if (nodo.getHoraLlegada() > i || nodo.estado == 4) {
                    continue;
                } else {
                    if (nodo.estado == 2 && nodo.getTiempoEjecucion() > 0) {
                        nodo.duracionBloqueo -= 1;
                    } else if (nodo.getTiempoEjecucion() > 0 && nodo.estado != 4) {
                        if (!turno) {
                            int menor = indiceMenorTiempoRestante(i,FCFS0);
                            if (FCFS0.get(menor).getTiempoEjecucion() > 0 && FCFS0.get(menor).estado != 2) {
                                if (FCFS0.get(menor).getNumero_de_Procesos() == nodo.getNumero_de_Procesos()) {
                                    ejecutado = false;
                                } else {
                                    ejecutado = true;
                                }
                            } else {
                                ejecutado = true;
                            }
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
                            nodo.setTiempoEjecucion(nodo.getTiempoEjecucion() - 1);
                            nodo.tiempoEjecucion += 1;
                        }
                        if (nodo.tiempoEjecucion == nodo.inicioBloqueo && nodo.duracionBloqueo != 0) {
                            nodo.estado = 2;
                        }
                        if (nodo.getTiempoEjecucion() == 0) {
                            nodo.estado = 4;
                            nodo.tiempoFinalizacion = i+1;
                        }
                    }
                }
            }
            for (Nodo nodo : FCFS0) {
                if (nodo.duracionBloqueo == 0 && nodo.estado == 2) {
                    nodo.estado = 3;
                }
            }
        }

        // Comenzar personalizacion
        setTitle("Algoritmo SRTF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 450);

        int Ncol = Numero_de_Procesos + 1;
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
                    case 1 -> datos[fil][col] = list.get(con).getTiempoEjecucion();
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

        // Crear tabla de datos
        JTable tb1 = new JTable(datos, columnas);
        JScrollPane panel = new JScrollPane(tb1);

        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int sizelist = list.size();
        int size = list.get(sizelist - 1).getT_Final();

        // Dibujar tabla
        int total = 40 * 22; /////////////////////////////////////// Numero de columnas
        g.drawLine(50, 249, (total + 50), 249);
        sizelist *= 22;
        g.drawLine(50, 250, 50, (sizelist + 250));

        // Dibujas los numeros de quantum
        int lim = 21;
        for (int ps = 0; total > 0; total -= 22, ps++) {
            String proc = Integer.toString(ps);
            if (ps == 0) {
                g.drawString(proc, 50, 248);
            } else {
                g.drawString(proc, (lim + 50), 248);
                lim += 22;
            }
        }
        lim = 22;
        for (int ps = 1; sizelist > 0; sizelist -= 22, ps++) {
            String proc = Integer.toString(ps);
            if (ps == 1) {
                g.drawString(proc, 41, 271);
            } else {
                g.drawString(proc, 41, (lim + 271));
                lim += 22;
            }
        }

        // Establecer color para cada proceso
        for (Nodo nodo : list) {
            
            nodo.color = new Color(0,143,57);
        }

        // Dibujar procesos
        int posY = 250, tfin = 0;
        int iniq, inip;

        int ultimaEjecucion = 0;

        for (int i = 0; i < 40; i++) {

            iniq = i * 22;

            boolean ejecutado = false, turno = false;

            for (Nodo nodo : this.list) {
                if (nodo.getNumero_de_Procesos() == 1) {
                    inip = 0;
                } else {
                    inip = (nodo.getNumero_de_Procesos() - 1) * 22;
                }

                int posX = 50;
                if (nodo.getHoraLlegada() > i || nodo.estado == 4) {
                    continue;
                } else {

                    if (nodo.estado == 2 && nodo.getTiempoEjecucion() > 0) {
                        nodo.duracionBloqueo -= 1;
                        g.setColor(Color.RED);
                        g.fillRect((posX + iniq), (posY + inip), 20, 20);

                    } else if (nodo.getTiempoEjecucion() > 0 && nodo.estado != 4) {

                        if (!turno) {

                            int menor = indiceMenorTiempoRestante(i,this.list);
                            if (list.get(menor).getTiempoEjecucion() > 0 && list.get(menor).estado != 2) {
                                if (list.get(menor).getNumero_de_Procesos() == nodo.getNumero_de_Procesos()) {
                                    ejecutado = false;
                                } else {
                                    ejecutado = true;
                                }
                            } else {
                                ejecutado = true;
                            }
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
                            nodo.setTiempoEjecucion(nodo.getTiempoEjecucion() - 1);
                            nodo.tiempoEjecucion += 1;
                        }

                        if (nodo.tiempoEjecucion == nodo.inicioBloqueo && nodo.duracionBloqueo != 0) {
                            nodo.estado = 2;
                        }
                        if (nodo.getTiempoEjecucion() == 0) {
                            nodo.estado = 4;
                        }
                    }
                }

            }

            for (Nodo nodo : list) {
                if (nodo.duracionBloqueo == 0 && nodo.estado == 2) {
                    nodo.estado = 3;
                }
            }

            detenerElTiempo();

        }
        try {
            Thread.sleep(1000000);
        } catch (Exception e) {
        }

    }

    public static void detenerElTiempo() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
        }
    }

    // Funcion que encuentra el menor tiempo restante
    public int indiceMenorTiempoRestante(int limite, ArrayList<Nodo> l) {
        int indice = 0;
        int menor = 10000;

        for (int i = 0; i < l.size(); i++) {
            Nodo n = l.get(i);
            if (n.getHoraLlegada() <= limite) {
                if (n.estado != 4 && n.estado != 2) {
                    if (n.getTiempoEjecucion() < menor) {
                        menor = n.getTiempoEjecucion();
                        indice = i;
                    }
                }
            }
        }
        return indice;
    }

}
