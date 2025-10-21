
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Principal extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JuegoLaberintoRPG juego = new JuegoLaberintoRPG();
            juego.setVisible(true);
        });
    }
}

/* ------------------------------------------------------
   CLASE PRINCIPAL DEL JUEGO (Ventana principal)
   ------------------------------------------------------ */
class JuegoLaberintoRPG extends JFrame {
    static final int TAMANO_LABERINTO = 25;
    static final int TAMANO_CASILLA = 24;
    static final int NUMERO_NIVELES = 4;

    PanelJuego panel;
    LogicaJuego logica;

    JuegoLaberintoRPG() {
        setTitle("Escape del Laberinto RPG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        logica = new LogicaJuego(TAMANO_LABERINTO, TAMANO_LABERINTO, NUMERO_NIVELES);
        panel = new PanelJuego(logica, TAMANO_CASILLA);
        add(panel);

        pack();
        setLocationRelativeTo(null);
    }
}

/* ------------------------------------------------------
   LÓGICA DEL JUEGO
   ------------------------------------------------------ */
class LogicaJuego {
    enum Casilla {
        MURO, CAMINO, LLAVE, PUERTA, LLAVE_TELETRANSPORTE, PUERTA_TELETRANSPORTE,
        PUNTO, TRAMPA, SALIDA, CURACION
    }

    final int filas, columnas;
    final int nivelMaximo;
    Casilla[][] mapa;
    boolean[][] descubierto;

    int jugadorX, jugadorY;
    int inicioX, inicioY;

    int vida;
    int vidas;
    int puntaje;
    int llaves;
    int llaveTeletransporte;
    int objetosCuracion;
    int nivel;

    Random aleatorio = new Random();

    LogicaJuego(int filas, int columnas, int nivelMaximo) {
        this.filas = filas;
        this.columnas = columnas;
        this.nivelMaximo = nivelMaximo;
        iniciarNuevoJuego();
    }

    void iniciarNuevoJuego() {
        nivel = 1;
        vidas = 3;
        puntaje = 0;
        iniciarNivel();
    }

    void iniciarNivel() {
        vida = 100;
        llaves = 0;
        llaveTeletransporte = 0;
        objetosCuracion = 0;
        mapa = new Casilla[filas][columnas];
        descubierto = new boolean[filas][columnas];

        generarLaberinto();
        colocarInicioJugador();
        colocarObjetosNivel();
    }

    /* --------------------------
       Generador aleatorio del laberinto
       -------------------------- */
    void generarLaberinto() {
        for (int f = 0; f < filas; f++)
            Arrays.fill(mapa[f], Casilla.MURO);

        boolean[][] tallado = new boolean[filas][columnas];
        int inicioX = 1, inicioY = 1;
        Deque<int[]> pila = new ArrayDeque<>();
        tallado[inicioY][inicioX] = true;
        mapa[inicioY][inicioX] = Casilla.CAMINO;
        pila.push(new int[]{inicioX, inicioY});

        int[][] direcciones = {{2,0},{-2,0},{0,2},{0,-2}};
        while (!pila.isEmpty()) {
            int[] actual = pila.peek();
            int cx = actual[0], cy = actual[1];

            java.util.List<int[]> vecinos = new ArrayList<>();
            for (int[] d : direcciones) {
                int nx = cx + d[0], ny = cy + d[1];
                if (nx > 0 && nx < columnas-1 && ny > 0 && ny < filas-1 && !tallado[ny][nx]) {
                    vecinos.add(new int[]{nx, ny, d[0], d[1]});
                }
            }

            if (!vecinos.isEmpty()) {
                int[] elegido = vecinos.get(aleatorio.nextInt(vecinos.size()));
                int nx = elegido[0], ny = elegido[1];
                int muroX = cx + elegido[2]/2;
                int muroY = cy + elegido[3]/2;
                mapa[muroY][muroX] = Casilla.CAMINO;
                mapa[ny][nx] = Casilla.CAMINO;
                tallado[ny][nx] = true;
                pila.push(new int[]{nx, ny});
            } else {
                pila.pop();
            }
        }
    }

    void colocarInicioJugador() {
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++) {
                if (mapa[f][c] == Casilla.CAMINO) {
                    inicioX = c; inicioY = f;
                    jugadorX = inicioX; jugadorY = inicioY;
                    descubierto[jugadorY][jugadorX] = true;
                    return;
                }
            }
    }

    void colocarObjetosNivel() {
        int cantidadPuntos = 30;
        int cantidadLlaves = 6;
        int cantidadPuertas = cantidadLlaves;
        int cantidadPuertasTele = 1;
        int trampasBase = 30;
        int cantidadCuraciones = 3;

        colocarAleatorio(Casilla.PUNTO, cantidadPuntos);
        colocarAleatorio(Casilla.LLAVE, cantidadLlaves);
        colocarAleatorio(Casilla.PUERTA, cantidadPuertas);
        colocarAleatorio(Casilla.LLAVE_TELETRANSPORTE, 1);
        colocarAleatorio(Casilla.PUERTA_TELETRANSPORTE, cantidadPuertasTele);
        int cantidadTrampas = trampasBase + (nivel - 1) * 10;
        colocarAleatorio(Casilla.TRAMPA, cantidadTrampas);
        colocarAleatorio(Casilla.CURACION, cantidadCuraciones);
        colocarAleatorio(Casilla.SALIDA, 1);

        mapa[inicioY][inicioX] = Casilla.CAMINO;
    }

    void colocarAleatorio(Casilla tipo, int cantidad) {
        int colocados = 0;
        int intentos = 0;
        while (colocados < cantidad && intentos < cantidad * 2000) {
            intentos++;
            int f = aleatorio.nextInt(filas);
            int c = aleatorio.nextInt(columnas);
            if (mapa[f][c] != Casilla.CAMINO) continue;
            if (f == inicioY && c == inicioX) continue;
            mapa[f][c] = tipo;
            colocados++;
        }
    }

    /* --------------------------
       Movimiento y acciones del jugador
       -------------------------- */
    void moverJugador(int dx, int dy) {
        int nx = jugadorX + dx, ny = jugadorY + dy;
        if (nx < 0 || nx >= columnas || ny < 0 || ny >= filas) return;
        Casilla casilla = mapa[ny][nx];

        if (casilla == Casilla.MURO) return;

        if (casilla == Casilla.PUERTA) {
            if (llaves > 0) {
                llaves--;
                mapa[ny][nx] = Casilla.CAMINO;
                casilla = Casilla.CAMINO;
            } else return;
        }

        if (casilla == Casilla.PUERTA_TELETRANSPORTE) {
            if (llaveTeletransporte > 0) {
                llaveTeletransporte--;
                avanzarNivel();
                return;
            } else return;
        }

        jugadorX = nx; jugadorY = ny;
        descubierto[jugadorY][jugadorX] = true;

        switch (casilla) {
            case LLAVE: llaves++; mapa[jugadorY][jugadorX] = Casilla.CAMINO; break;
            case LLAVE_TELETRANSPORTE: llaveTeletransporte++; mapa[jugadorY][jugadorX] = Casilla.CAMINO; break;
            case PUNTO: puntaje += 10; mapa[jugadorY][jugadorX] = Casilla.CAMINO; break;
            case TRAMPA:
                vida -= 33; mapa[jugadorY][jugadorX] = Casilla.CAMINO;
                if (vida <= 0) morir();
                break;
            case CURACION: objetosCuracion++; mapa[jugadorY][jugadorX] = Casilla.CAMINO; break;
            case SALIDA: avanzarNivel(); break;
            default: break;
        }
    }

    void avanzarNivel() {
        if (nivel >= nivelMaximo) {
            JOptionPane.showMessageDialog(null,
                "¡Has completado el juego!\nPuntuación final: " + puntaje,
                "¡Felicidades!", JOptionPane.INFORMATION_MESSAGE);
            iniciarNuevoJuego();
        } else {
            nivel++;
            vida = 100;
            iniciarNivel();
        }
    }

    void morir() {
        vidas--;
        if (vidas <= 0) {
            JOptionPane.showMessageDialog(null, "Perdiste tus 3 vidas. Reiniciando el juego.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            iniciarNuevoJuego();
        } else {
            JOptionPane.showMessageDialog(null, "Has muerto. Te quedan " + vidas + " vidas.", "Derrota", JOptionPane.INFORMATION_MESSAGE);
            for (int f = 0; f < filas; f++)
                Arrays.fill(descubierto[f], false);
            jugadorX = inicioX; jugadorY = inicioY;
            descubierto[jugadorY][jugadorX] = true;
            vida = 100;
        }
    }

    void usarCuracion() {
        if (objetosCuracion > 0 && vida < 100) {
            objetosCuracion--;
            vida += 50;
            if (vida > 100) vida = 100;
        }
    }
}

/* ------------------------------------------------------
   PANEL DE DIBUJO Y CONTROLES
   ------------------------------------------------------ */
class PanelJuego extends JPanel implements KeyListener {
    LogicaJuego juego;
    int tamanoCasilla;
    Font fuenteHUD = new Font("SansSerif", Font.BOLD, 12);

    PanelJuego(LogicaJuego juego, int tamanoCasilla) {
        this.juego = juego;
        this.tamanoCasilla = tamanoCasilla;
        setPreferredSize(new Dimension(juego.columnas * tamanoCasilla, juego.filas * tamanoCasilla + 70));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Dibujar laberinto
        for (int f = 0; f < juego.filas; f++) {
            for (int c = 0; c < juego.columnas; c++) {
                int x = c * tamanoCasilla;
                int y = f * tamanoCasilla;
                boolean visto = juego.descubierto[f][c];
                if (!visto) {
                    g2.setColor(new Color(10,10,10));
                    g2.fillRect(x, y, tamanoCasilla, tamanoCasilla);
                    continue;
                }

                switch (juego.mapa[f][c]) {
                    case MURO -> g2.setColor(Color.DARK_GRAY);
                    case CAMINO -> g2.setColor(new Color(50,50,60));
                    case LLAVE -> g2.setColor(Color.YELLOW);
                    case PUERTA -> g2.setColor(new Color(80,40,0));
                    case LLAVE_TELETRANSPORTE -> g2.setColor(Color.CYAN);
                    case PUERTA_TELETRANSPORTE -> g2.setColor(Color.GREEN);
                    case PUNTO -> g2.setColor(Color.MAGENTA);
                    case TRAMPA -> g2.setColor(Color.RED);
                    case SALIDA -> g2.setColor(Color.BLUE);
                    case CURACION -> g2.setColor(Color.PINK);
                }
                g2.fillRect(x, y, tamanoCasilla, tamanoCasilla);
            }
        }

        // Jugador
        int px = juego.jugadorX * tamanoCasilla;
        int py = juego.jugadorY * tamanoCasilla;
        juego.descubierto[juego.jugadorY][juego.jugadorX] = true;
        g2.setColor(Color.WHITE);
        g2.fillOval(px+4, py+4, tamanoCasilla-8, tamanoCasilla-8);

        // HUD
        int hudY = juego.filas * tamanoCasilla + 15;
        g2.setColor(Color.WHITE);
        g2.setFont(fuenteHUD);
        g2.drawString("HP: " + juego.vida + "/100", 10, hudY);
        g2.drawString("Vidas: " + juego.vidas, 110, hudY);
        g2.drawString("Llaves: " + juego.llaves, 200, hudY);
        g2.drawString("LlaveT: " + juego.llaveTeletransporte, 290, hudY);
        g2.drawString("Curaciones: " + juego.objetosCuracion, 380, hudY);
        g2.drawString("Nivel: " + juego.nivel + "/" + juego.nivelMaximo, 500, hudY);
        g2.drawString("Puntaje: " + juego.puntaje, 600, hudY);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        if (tecla == KeyEvent.VK_W || tecla == KeyEvent.VK_UP) juego.moverJugador(0,-1);
        if (tecla == KeyEvent.VK_S || tecla == KeyEvent.VK_DOWN) juego.moverJugador(0,1);
        if (tecla == KeyEvent.VK_A || tecla == KeyEvent.VK_LEFT) juego.moverJugador(-1,0);
        if (tecla == KeyEvent.VK_D || tecla == KeyEvent.VK_RIGHT) juego.moverJugador(1,0);
        if (tecla == KeyEvent.VK_E) juego.usarCuracion();
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}

