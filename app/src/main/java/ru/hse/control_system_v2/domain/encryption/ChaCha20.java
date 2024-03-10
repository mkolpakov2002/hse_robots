package ru.hse.control_system_v2.domain.encryption;// ChaCha 256-bit Cipher

public class ChaCha20 {
    //Размер ключа в байтах
    public static final int KEY_SIZE = 32;

    //Размер одноразового номера в байтах
    public static final int NONCE_SIZE = 12;

    private int[] matrix = new int[16];

    //Преобразует формат Little Endian в целочисленные данные
    protected static int littleEndianToInt(byte[] bs, int i) {
        return (bs[i] & 0xff) | ((bs[i + 1] & 0xff) << 8) | ((bs[i + 2] & 0xff) << 16) | ((bs[i + 3] & 0xff) << 24);
    }

    //Преобразует целочисленные данные в формат с прямым порядком байтов
    protected static void intToLittleEndian(int n, byte[] bs, int off) {
        bs[  off] = (byte)(n       );
        bs[++off] = (byte)(n >>>  8);
        bs[++off] = (byte)(n >>> 16);
        bs[++off] = (byte)(n >>> 24);
    }

    //Метод поворота
    protected static int ROTATE(int v, int c) {
        return (v << c) | (v >>> (32 - c));
    }

    //Четверть раунда, как описано в оригинальной статье
    protected static void quarterRound(int[] x, int a, int b, int c, int d) {
        x[a] += x[b];
        x[d] = ROTATE(x[d] ^ x[a], 16);
        x[c] += x[d];
        x[b] = ROTATE(x[b] ^ x[c], 12);
        x[a] += x[b];
        x[d] = ROTATE(x[d] ^ x[a], 8);
        x[c] += x[d];
        x[b] = ROTATE(x[b] ^ x[c], 7);
    }


    public ChaCha20(byte[] key, byte[] nonce, int counter){

        this.matrix[ 0] = 0x61707865;
        this.matrix[ 1] = 0x3320646e;
        this.matrix[ 2] = 0x79622d32;
        this.matrix[ 3] = 0x6b206574;
        this.matrix[ 4] = littleEndianToInt(key, 0);
        this.matrix[ 5] = littleEndianToInt(key, 4);
        this.matrix[ 6] = littleEndianToInt(key, 8);
        this.matrix[ 7] = littleEndianToInt(key, 12);
        this.matrix[ 8] = littleEndianToInt(key, 16);
        this.matrix[ 9] = littleEndianToInt(key, 20);
        this.matrix[10] = littleEndianToInt(key, 24);
        this.matrix[11] = littleEndianToInt(key, 28);

        if (nonce.length == NONCE_SIZE) {
            this.matrix[12] = counter;
            this.matrix[13] = littleEndianToInt(nonce, 0);
            this.matrix[14] = littleEndianToInt(nonce, 4);
            this.matrix[15] = littleEndianToInt(nonce, 8);
        }
    }

    //Метод шифрования ChaCha20
    public void encrypt(byte[] dst, byte[] src, int len) {
        int[] x = new int[16];
        byte[] output = new byte[64];
        int i, dpos = 0, spos = 0;

        while (len > 0) {
            for (i = 16; i-- > 0; ) x[i] = this.matrix[i];

            //Повторить 20 раундов
            for (i = 20; i > 0; i -= 2) {
                quarterRound(x, 0, 4,  8, 12);
                quarterRound(x, 1, 5,  9, 13);
                quarterRound(x, 2, 6, 10, 14);
                quarterRound(x, 3, 7, 11, 15);
                quarterRound(x, 0, 5, 10, 15);
                quarterRound(x, 1, 6, 11, 12);
                quarterRound(x, 2, 7,  8, 13);
                quarterRound(x, 3, 4,  9, 14);
            }

            //Добавить выходные слова в исходную матрицу
            for (i = 16; i-- > 0; ) x[i] += this.matrix[i];
            for (i = 16; i-- > 0; ) intToLittleEndian(x[i], output, 4 * i);

            //Увеличение счетчика и обработка переполнения
            this.matrix[12] += 1;
            if (this.matrix[12] <= 0) {
                this.matrix[13] += 1;
            }

            // Генерация выходных данных с использованием значений матрицы и входных данных
            if (len <= 64) {
                // Вводится только для неполных блоков с менее чем 64 байтами данных
                for (i = len; i-- > 0; ) {
                    dst[i + dpos] = (byte) (src[i + spos] ^ output[i]);
                }
                return;
            }

            //Цикл XOR для полных 64-байтовых блоков
            for (i = 64; i-- > 0; ) {
                dst[i + dpos] = (byte) (src[i + spos] ^ output[i]);
            }

            //Уменьшить длину
            //Увеличиваем указатели источника и назначения
            len -= 64;
            spos += 64;
            dpos += 64;
        }
    }

    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase());

        }
        System.out.println();
    }
}
