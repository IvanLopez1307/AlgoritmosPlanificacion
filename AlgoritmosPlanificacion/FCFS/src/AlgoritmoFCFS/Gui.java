package AlgoritmoFCFS;

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

        while (!procesos.isEmpty()) { // mientras la lista de procesos no esté vacía
            Nodo procesoMasAntiguo = procesos.get(0); // se toma el primer proceso como el más antiguo
            int pos = 0; // se guarda la posición del proceso más antiguo en la lista
            for (int i = 1; i < procesos.size(); i++) { // se recorre la lista a partir del segundo proceso
                Nodo procesoActual = procesos.get(i); // se obtiene el proceso actual
                if (procesoActual.getHoraLlegada() < procesoMasAntiguo.getHoraLlegada()) { // si el proceso actual es más antiguo que el proceso más antiguo hasta ahora
                    procesoMasAntiguo = procesoActual; // se actualiza el proceso más antiguo
                    pos = i; // se guarda la posición del proceso más antiguo en la lista
                }
            }
            FCFS0.add(procesos.remove(pos)); // se elimina el proceso más antiguo de la lista de procesos y se agrega a la lista de procesos ordenados por FCFS
        }

        // Se inicializa la variable on en true para entrar al ciclo while
        on = true;
        while (on) {
            int mini = Integer.MAX_VALUE; // Se inicializa mini con el valor máximo que puede tener un entero
            int pos = -1; // Se inicializa pos con -1 para verificar si se encontró algún proceso
            for (int i = 0; i < FCFS0.size(); i++) { // Se recorre la lista FCFS0
                Nodo nodo = FCFS0.get(i); // Se obtiene el nodo actual
                if (nodo.getHoraLlegada() <= mini) { // Si la hora de llegada del nodo es menor o igual a mini
                    mini = nodo.getHoraLlegada(); // Se actualiza el valor de mini con la hora de llegada del nodo
                    pos = i; // Se guarda la posición del nodo en la lista FCFS0
                }
            }
            if (pos != -1) { // Si se encontró algún proceso en FCFS0
                FCFS.add(FCFS0.remove(pos)); // Se remueve el proceso de FCFS0 y se agrega a FCFS
            } else { // Si no se encontró ningún proceso en FCFS0
                on = false; // Se sale del ciclo while
            }
        }

        // Se generan las medidas generales necesarias para la impresión
// TimF es el tiempo final, num es el número del proceso actual
        int TimF = 0, num = 1;

// Se recorre la lista de procesos FCFS
        for (Nodo nodo : FCFS) {
// Se asigna un número de proceso a cada nodo
            nodo.setNumero_de_Procesos(num++);
// Se actualiza el tiempo final con el tiempo de ejecución del proceso actual
            TimF += nodo.getTiempoEjecucion();

// Se asigna el tiempo final y el tiempo de retorno al nodo actual
            nodo.setT_Final(TimF);
            nodo.setT_Retorno(TimF - nodo.getHoraLlegada());
        }

        // Ajustar arraylists para las metricas
        FCFS0.clear();
        FCFS0.addAll(FCFS);
        for (Nodo n : FCFS) {
            list.add(n.copiarNodo());
        }

        // Recolectar datos y metricas de cada proceso
        int ultimaEjecucion = 0;
        Color c;
        for (int i = 0; i < 40; i++) {
            boolean ejecutado, turno = false;
            for (Nodo nodo : FCFS0) {
                if (nodo.getHoraLlegada() > i || nodo.estado == 4) {
                    continue;
                } else {
                    if (nodo.estado == 2 && nodo.getTiempoEjecucion() > 0) {
                        nodo.duracionBloqueo -= 1;
                        if (nodo.duracionBloqueo == 0) {
                            nodo.estado = 3;
                        }
                    } else if (nodo.getTiempoEjecucion() > 0 && nodo.estado != 4) {
                        if (!turno) {
                            if (ultimaEjecucion != 0) {
                                if (FCFS0.get(ultimaEjecucion - 1).getTiempoEjecucion() > 0 && FCFS0.get(ultimaEjecucion - 1).estado != 2) {
                                    if (FCFS0.get(ultimaEjecucion - 1).getNumero_de_Procesos() == nodo.getNumero_de_Procesos()) {
                                        ejecutado = false;
                                    } else {
                                        ejecutado = true;
                                    }
                                } else {
                                    ejecutado = false;
                                    ultimaEjecucion = 0;
                                }
                            } else {
                                ejecutado = false;
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
                            nodo.tiempoFinalizacion = i + 1;
                        }
                    }
                }
            }
        }

        // Comenzar personalizacion de la tabla 
        setTitle("Algoritmo FCFS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 450);

        // Contenido del JTable
        int Ncol = Numero_de_Procesos + 1;
        datos = new Object[9][Ncol];
        for (int fil = 0; fil < 9; fil++) {
            switch (fil) {
                case 0 -> datos[fil][0] = "Hora de llegada: ";
                case 1 -> datos[fil][0] = "Tiempo ejecución: ";
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
                    case 8 -> datos[fil][col] = (FCFS0.get(con).tiempoFinalizacion - FCFS.get(con).getHoraLlegada()) / (float) list.get(con).getTiempoEjecucion();
                }
            }
        }

        // Agregar numeros de procesos segun la columna
        int in = 0;
        columnas = new Object[Ncol];
        for (int i = 0; i < Ncol; i++) {
            if (i == 0) {
                columnas[i] = "Numero de proceso:";
            } else {
                columnas[i] = FCFS.get(in).getNumero_de_Procesos();
                in++;
            }
        }

        // Agregar tabla a la interfaz
        tb1 = new JTable(datos, columnas);
        JScrollPane panel = new JScrollPane(tb1);
        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Colorear bloques de proceso
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int sizelist = list.size();

        // Tabla
        int total = 40 * 22; /////////////////////////////////////// Numero de columnas
        g.drawLine(50, 249, (total + 50), 249);
        sizelist *= 22;
        g.drawLine(50, 250, 50, (sizelist + 250));

        
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
            nodo.color = new Color(0, 143, 57);
        }

        // Dibujar procesos
        int posY = 250, tfin = 0;
        int iniq, inip;

        int ultimaEjecucion = 0;

        for (int i = 0; i < 40; i++) {

            iniq = i * 22;

            boolean ejecutado = false, turno = false;

            for (Nodo nodo : list) {
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

                        if (nodo.duracionBloqueo == 0) {
                            nodo.estado = 3;
                        }

                    } else if (nodo.getTiempoEjecucion() > 0 && nodo.estado != 4) {

                        if (!turno) {
                            if (ultimaEjecucion != 0) {
                                if (list.get(ultimaEjecucion - 1).getTiempoEjecucion() > 0 && list.get(ultimaEjecucion - 1).estado != 2) {
                                    if (list.get(ultimaEjecucion - 1).getNumero_de_Procesos() == nodo.getNumero_de_Procesos()) {
                                        ejecutado = false;

                                    } else {
                                        ejecutado = true;
                                    }
                                } else {
                                    ejecutado = false;
                                    ultimaEjecucion = 0;
                                }

                            } else {
                                ejecutado = false;
                            }
                        } else {
                            ejecutado = true;
                        }

                        if (ejecutado) {
                            g.setColor(Color.GRAY);
                            nodo.tiempoDeEspera += 1;
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
                            nodo.tiempoFinalizacion = i;
                        }
                    }
                }
            }
            // Simular un segundo de congelamiento
            detenerElTiempo();
        }
        // Evitar que la pantalla vuelva a ejecutar Paint()
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

}
