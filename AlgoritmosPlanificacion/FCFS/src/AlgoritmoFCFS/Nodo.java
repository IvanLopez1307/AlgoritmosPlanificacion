
package AlgoritmoFCFS;

import java.awt.Color;


public class Nodo{
    
   
    private int Numero_de_Procesos;
    private int HoraLlegada;
    private int TiempoEjecucion;
    int tiempoEjecucion = 0;
    int inicioBloqueo = 0;
    int duracionBloqueo = 0;
    Color color;
    private int T_Final;
    private int T_Retorno;
    int tiempoDeEspera = 0;
    int tiempoFinalizacion;
    int estado = 0;
    
    public Nodo(Nodo n){
        this.HoraLlegada = n.HoraLlegada;
        this.TiempoEjecucion = n.TiempoEjecucion;
        this.Numero_de_Procesos = n.getNumero_de_Procesos();
        this.duracionBloqueo = n.duracionBloqueo;
        this.inicioBloqueo = n.inicioBloqueo;
        this.T_Final = n.getT_Final();
        this.T_Retorno = n.getT_Retorno();
    }
    
    public Nodo copiarNodo(){
        return new Nodo(this);
    }
    
    public Nodo (int a, int b){
        HoraLlegada = a;
        TiempoEjecucion = b;
    }

    public void setHoraLlegada(int HoraLlegada) {
        this.HoraLlegada = HoraLlegada;
    }

    public void setNumero_de_Procesos(int Numero_de_Procesos) {
        this.Numero_de_Procesos = Numero_de_Procesos;
    }

    public void setT_Final(int T_Final) {
        this.T_Final = T_Final;
    }

    public void setT_Retorno(int T_Retorno) {
        this.T_Retorno = T_Retorno;
    }

    public void setTiempoEjecucion(int TiempoEjecucion) {
        if (T_Retorno == 0){
            estado = 4;
        }
        this.TiempoEjecucion = TiempoEjecucion;
    }

    public int getHoraLlegada() {
        return HoraLlegada;
    }

    public int getNumero_de_Procesos() {
        return Numero_de_Procesos;
    }

    public int getT_Final() {
        return T_Final;
    }


    public int getT_Retorno() {
        return T_Retorno;
    }

    public int getTiempoEjecucion() {
        return TiempoEjecucion;
    }
    
}
