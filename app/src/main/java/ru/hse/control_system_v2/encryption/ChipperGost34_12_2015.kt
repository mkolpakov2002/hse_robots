package ru.hse.control_system_v2.encryption

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom


class ChipperGost34_12_2015 private constructor(key: ByteArray, iVector: ByteArray) {
    private val key: ByteArray
    private val iVector: ByteArray
    private val roundConsts = Array(32) {
        ByteArray(
            16
        )
    }
    private val roundKeys = Array<ByteArray?>(10) {
        ByteArray(
            16
        )
    }
    private var stopCTR = 0
    private var T: MutableSet<ByteArray>? = null

    //длина блока данных
    private val BLOCK_SIZE_BYTES = 16

    //таблица для нелинейного биективного преобразования
    private val Pi = byteArrayOf(
        0xFC.toByte(),
        0xEE.toByte(),
        0xDD.toByte(),
        0x11,
        0xCF.toByte(),
        0x6E,
        0x31,
        0x16,
        0xFB.toByte(),
        0xC4.toByte(),
        0xFA.toByte(),
        0xDA.toByte(),
        0x23,
        0xC5.toByte(),
        0x04,
        0x4D,
        0xE9.toByte(),
        0x77,
        0xF0.toByte(),
        0xDB.toByte(),
        0x93.toByte(),
        0x2E,
        0x99.toByte(),
        0xBA.toByte(),
        0x17,
        0x36,
        0xF1.toByte(),
        0xBB.toByte(),
        0x14,
        0xCD.toByte(),
        0x5F,
        0xC1.toByte(),
        0xF9.toByte(),
        0x18,
        0x65,
        0x5A,
        0xE2.toByte(),
        0x5C,
        0xEF.toByte(),
        0x21,
        0x81.toByte(),
        0x1C,
        0x3C,
        0x42,
        0x8B.toByte(),
        0x01,
        0x8E.toByte(),
        0x4F,
        0x05,
        0x84.toByte(),
        0x02,
        0xAE.toByte(),
        0xE3.toByte(),
        0x6A,
        0x8F.toByte(),
        0xA0.toByte(),
        0x06,
        0x0B,
        0xED.toByte(),
        0x98.toByte(),
        0x7F,
        0xD4.toByte(),
        0xD3.toByte(),
        0x1F,
        0xEB.toByte(),
        0x34,
        0x2C,
        0x51,
        0xEA.toByte(),
        0xC8.toByte(),
        0x48,
        0xAB.toByte(),
        0xF2.toByte(),
        0x2A,
        0x68,
        0xA2.toByte(),
        0xFD.toByte(),
        0x3A,
        0xCE.toByte(),
        0xCC.toByte(),
        0xB5.toByte(),
        0x70,
        0x0E,
        0x56,
        0x08,
        0x0C,
        0x76,
        0x12,
        0xBF.toByte(),
        0x72,
        0x13,
        0x47,
        0x9C.toByte(),
        0xB7.toByte(),
        0x5D,
        0x87.toByte(),
        0x15,
        0xA1.toByte(),
        0x96.toByte(),
        0x29,
        0x10,
        0x7B,
        0x9A.toByte(),
        0xC7.toByte(),
        0xF3.toByte(),
        0x91.toByte(),
        0x78,
        0x6F,
        0x9D.toByte(),
        0x9E.toByte(),
        0xB2.toByte(),
        0xB1.toByte(),
        0x32,
        0x75,
        0x19,
        0x3D,
        0xFF.toByte(),
        0x35,
        0x8A.toByte(),
        0x7E,
        0x6D,
        0x54,
        0xC6.toByte(),
        0x80.toByte(),
        0xC3.toByte(),
        0xBD.toByte(),
        0x0D,
        0x57,
        0xDF.toByte(),
        0xF5.toByte(),
        0x24,
        0xA9.toByte(),
        0x3E,
        0xA8.toByte(),
        0x43.toByte(),
        0xC9.toByte(),
        0xD7.toByte(),
        0x79,
        0xD6.toByte(),
        0xF6.toByte(),
        0x7C,
        0x22,
        0xB9.toByte(),
        0x03,
        0xE0.toByte(),
        0x0F,
        0xEC.toByte(),
        0xDE.toByte(),
        0x7A,
        0x94.toByte(),
        0xB0.toByte(),
        0xBC.toByte(),
        0xDC.toByte(),
        0xE8.toByte(),
        0x28,
        0x50,
        0x4E,
        0x33,
        0x0A,
        0x4A,
        0xA7.toByte(),
        0x97.toByte(),
        0x60,
        0x73,
        0x1E,
        0x00,
        0x62,
        0x44,
        0x1A,
        0xB8.toByte(),
        0x38,
        0x82.toByte(),
        0x64,
        0x9F.toByte(),
        0x26,
        0x41,
        0xAD.toByte(),
        0x45,
        0x46,
        0x92.toByte(),
        0x27,
        0x5E,
        0x55,
        0x2F,
        0x8C.toByte(),
        0xA3.toByte(),
        0xA5.toByte(),
        0x7D,
        0x69,
        0xD5.toByte(),
        0x95.toByte(),
        0x3B,
        0x07,
        0x58,
        0xB3.toByte(),
        0x40,
        0x86.toByte(),
        0xAC.toByte(),
        0x1D,
        0xF7.toByte(),
        0x30,
        0x37,
        0x6B,
        0xE4.toByte(),
        0x88.toByte(),
        0xD9.toByte(),
        0xE7.toByte(),
        0x89.toByte(),
        0xE1.toByte(),
        0x1B,
        0x83.toByte(),
        0x49,
        0x4C,
        0x3F,
        0xF8.toByte(),
        0xFE.toByte(),
        0x8D.toByte(),
        0x53,
        0xAA.toByte(),
        0x90.toByte(),
        0xCA.toByte(),
        0xD8.toByte(),
        0x85.toByte(),
        0x61,
        0x20,
        0x71,
        0x67,
        0xA4.toByte(),
        0x2D,
        0x2B,
        0x09,
        0x5B,
        0xCB.toByte(),
        0x9B.toByte(),
        0x25,
        0xD0.toByte(),
        0xBE.toByte(),
        0xE5.toByte(),
        0x6C,
        0x52,
        0x59,
        0xA6.toByte(),
        0x74,
        0xD2.toByte(),
        0xE6.toByte(),
        0xF4.toByte(),
        0xB4.toByte(),
        0xC0.toByte(),
        0xD1.toByte(),
        0x66,
        0xAF.toByte(),
        0xC2.toByte(),
        0x39,
        0x4B,
        0x63,
        0xB6.toByte()
    )

    //таблица для нелинейного биективного преобразования для расшифрования
    private val rPi = byteArrayOf(
        0xA5.toByte(),
        0x2D,
        0x32,
        0x8F.toByte(),
        0x0E,
        0x30,
        0x38,
        0xC0.toByte(),
        0x54,
        0xE6.toByte(),
        0x9E.toByte(),
        0x39,
        0x55,
        0x7E,
        0x52,
        0x91.toByte(),
        0x64,
        0x03,
        0x57,
        0x5A,
        0x1C,
        0x60,
        0x07,
        0x18,
        0x21,
        0x72,
        0xA8.toByte(),
        0xD1.toByte(),
        0x29,
        0xC6.toByte(),
        0xA4.toByte(),
        0x3F,
        0xE0.toByte(),
        0x27,
        0x8D.toByte(),
        0x0C,
        0x82.toByte(),
        0xEA.toByte(),
        0xAE.toByte(),
        0xB4.toByte(),
        0x9A.toByte(),
        0x63,
        0x49,
        0xE5.toByte(),
        0x42,
        0xE4.toByte(),
        0x15,
        0xB7.toByte(),
        0xC8.toByte(),
        0x06,
        0x70,
        0x9D.toByte(),
        0x41,
        0x75,
        0x19,
        0xC9.toByte(),
        0xAA.toByte(),
        0xFC.toByte(),
        0x4D,
        0xBF.toByte(),
        0x2A,
        0x73,
        0x84.toByte(),
        0xD5.toByte(),
        0xC3.toByte(),
        0xAF.toByte(),
        0x2B,
        0x86.toByte(),
        0xA7.toByte(),
        0xB1.toByte(),
        0xB2.toByte(),
        0x5B,
        0x46,
        0xD3.toByte(),
        0x9F.toByte(),
        0xFD.toByte(),
        0xD4.toByte(),
        0x0F,
        0x9C.toByte(),
        0x2F,
        0x9B.toByte(),
        0x43,
        0xEF.toByte(),
        0xD9.toByte(),
        0x79,
        0xB6.toByte(),
        0x53,
        0x7F,
        0xC1.toByte(),
        0xF0.toByte(),
        0x23,
        0xE7.toByte(),
        0x25,
        0x5E,
        0xB5.toByte(),
        0x1E,
        0xA2.toByte(),
        0xDF.toByte(),
        0xA6.toByte(),
        0xFE.toByte(),
        0xAC.toByte(),
        0x22,
        0xF9.toByte(),
        0xE2.toByte(),
        0x4A,
        0xBC.toByte(),
        0x35,
        0xCA.toByte(),
        0xEE.toByte(),
        0x78,
        0x05,
        0x6B,
        0x51,
        0xE1.toByte(),
        0x59,
        0xA3.toByte(),
        0xF2.toByte(),
        0x71,
        0x56,
        0x11,
        0x6A,
        0x89.toByte(),
        0x94.toByte(),
        0x65,
        0x8C.toByte(),
        0xBB.toByte(),
        0x77,
        0x3C,
        0x7B,
        0x28,
        0xAB.toByte(),
        0xD2.toByte(),
        0x31,
        0xDE.toByte(),
        0xC4.toByte(),
        0x5F,
        0xCC.toByte(),
        0xCF.toByte(),
        0x76,
        0x2C,
        0xB8.toByte(),
        0xD8.toByte(),
        0x2E,
        0x36,
        0xDB.toByte(),
        0x69,
        0xB3.toByte(),
        0x14,
        0x95.toByte(),
        0xBE.toByte(),
        0x62,
        0xA1.toByte(),
        0x3B,
        0x16,
        0x66,
        0xE9.toByte(),
        0x5C,
        0x6C,
        0x6D,
        0xAD.toByte(),
        0x37,
        0x61,
        0x4B,
        0xB9.toByte(),
        0xE3.toByte(),
        0xBA.toByte(),
        0xF1.toByte(),
        0xA0.toByte(),
        0x85.toByte(),
        0x83.toByte(),
        0xDA.toByte(),
        0x47,
        0xC5.toByte(),
        0xB0.toByte(),
        0x33,
        0xFA.toByte(),
        0x96.toByte(),
        0x6F,
        0x6E,
        0xC2.toByte(),
        0xF6.toByte(),
        0x50,
        0xFF.toByte(),
        0x5D,
        0xA9.toByte(),
        0x8E.toByte(),
        0x17,
        0x1B,
        0x97.toByte(),
        0x7D,
        0xEC.toByte(),
        0x58,
        0xF7.toByte(),
        0x1F,
        0xFB.toByte(),
        0x7C,
        0x09,
        0x0D,
        0x7A,
        0x67,
        0x45,
        0x87.toByte(),
        0xDC.toByte(),
        0xE8.toByte(),
        0x4F,
        0x1D,
        0x4E,
        0x04,
        0xEB.toByte(),
        0xF8.toByte(),
        0xF3.toByte(),
        0x3E,
        0x3D,
        0xBD.toByte(),
        0x8A.toByte(),
        0x88.toByte(),
        0xDD.toByte(),
        0xCD.toByte(),
        0x0B,
        0x13,
        0x98.toByte(),
        0x02,
        0x93.toByte(),
        0x80.toByte(),
        0x90.toByte(),
        0xD0.toByte(),
        0x24,
        0x34,
        0xCB.toByte(),
        0xED.toByte(),
        0xF4.toByte(),
        0xCE.toByte(),
        0x99.toByte(),
        0x10,
        0x44,
        0x40,
        0x92.toByte(),
        0x3A,
        0x01,
        0x26,
        0x12,
        0x1A,
        0x48,
        0x68,
        0xF5.toByte(),
        0x81.toByte(),
        0x8B.toByte(),
        0xC7.toByte(),
        0xD6.toByte(),
        0x20,
        0x0A,
        0x08,
        0x00,
        0x4C,
        0xD7.toByte(),
        0x74
    )

    //линейное преобразование в поле Галуа
    private val lVector = byteArrayOf(
        1,
        148.toByte(),
        32,
        133.toByte(),
        16,
        194.toByte(),
        192.toByte(),
        1,
        251.toByte(),
        1,
        192.toByte(),
        194.toByte(),
        16,
        133.toByte(),
        32,
        148.toByte()
    )

    init {
        require(!(key.size != 32 || iVector.size != 16)) { "Wrong size of key or iVector" }
        this.key = key
        this.iVector = iVector
        val leftPart = ByteArray(16)
        val rightPart = ByteArray(16)
        System.arraycopy(this.key, 0, leftPart, 0, 16)
        System.arraycopy(this.key, 16, rightPart, 0, 16)
        initRoundKeys(leftPart, rightPart)
    }

    //Наложение раундового ключа 〖𝑘∈𝑉〗_128  на блок данных
    private fun XOR(left: ByteArray?, right: ByteArray?): ByteArray {
        val result = ByteArray(BLOCK_SIZE_BYTES)
        for (i in result.indices) result[i] = (left!![i].toInt() xor right!![i].toInt()).toByte()
        return result
    }

    //Замена байтов в блоке данных
    private fun S(input: ByteArray): ByteArray {
        val output = ByteArray(input.size)
        for (i in 0 until BLOCK_SIZE_BYTES) {
            var data = input[i].toInt()
            if (data < 0) data += 256
            output[i] = Pi[data]
        }
        return output
    }

    //Перемешивание блока данных
    private fun multiplicationGF(left: Byte, right: Byte): Byte {
        var left = left
        var right = right
        var result: Byte = 0
        var hBit: Byte
        for (i in 0..7) {
            if (right.toInt() and 1 == 1) result = (result.toInt() xor left.toInt()).toByte()
            hBit = (left.toInt() and 0x80).toByte()
            left = (left.toInt() shl 1).toByte()
            if (hBit < 0) left = (left.toInt() xor 0xC3).toByte()
            right = (right.toInt() shr 1).toByte()
        }
        return result
    }

    //𝑅(𝑎)=𝑅(𝑎_15 ||…||𝑎_0 )=𝑙(𝑎_15 ||𝑎_14 ||…||𝑎_0 ) 〖||𝑎〗_15 ||…||𝑎_1
    private fun R(input: ByteArray): ByteArray {
        var a15: Byte = 0
        val output = ByteArray(16)
        for (i in 15 downTo 0) {
            if (i == 0) output[15] = input[i] else output[i - 1] = input[i]
            a15 = (a15.toInt() xor multiplicationGF(input[i], lVector[i]).toInt()).toByte()
        }
        output[15] = a15
        return output
    }

    //𝐿(𝑎)=𝑅^16 (𝑎)
    private fun L(input: ByteArray): ByteArray {
        var output = input
        for (i in 0..15) output = R(output)
        return output
    }

    //𝑆^(−1) для расшифрования
    private fun rS(input: ByteArray): ByteArray {
        val output = ByteArray(input.size)
        for (i in 0 until BLOCK_SIZE_BYTES) {
            var data = input[i].toInt()
            if (data < 0) data += 256
            output[i] = rPi[data]
        }
        return output
    }

    //R^(−1) для расшифрования
    private fun rR(input: ByteArray): ByteArray {
        var a0 = input[15]
        val output = ByteArray(16)
        for (i in 1..15) {
            output[i] = input[i - 1]
            a0 = (a0.toInt() xor multiplicationGF(output[i], lVector[i]).toInt()).toByte()
        }
        output[0] = a0
        return output
    }

    //L^(−1) для расшифрования
    private fun rL(input: ByteArray): ByteArray {
        var output = input
        for (i in 0..15) output = rR(output)
        return output
    }

    //Вычисление раундовых констант
    private fun initRoundConsts() {
        val roundNum = Array(32) {
            ByteArray(
                16
            )
        }
        for (i in 0..31) {
//            for (int j = 0; j < BLOCK_SIZE_BYTES; j++)
//                roundNum[i][j] = 0;
            roundNum[i][0] = (i + 1).toByte()
        }
        for (i in 0..31) roundConsts[i] = L(roundNum[i])
    }

    //преобразование 8-разрядной строки в элемент поля Галуа.
    //𝛷𝛹		композиция отображений 𝛷 и 𝛹
    private fun FeistelRound(
        inLeft: ByteArray?,
        inRight: ByteArray?,
        roundC: ByteArray
    ): Array<ByteArray?> {
        var temp: ByteArray
        temp = XOR(inLeft, roundC)
        temp = S(temp)
        temp = L(temp)
        val outLeft = XOR(temp, inRight)
        val result = arrayOfNulls<ByteArray>(2)
        result[0] = outLeft
        result[1] = inLeft
        return result
    }

    //Вычисление первых двух раундовых ключей как двух частей ключа шифрования
    private fun initRoundKeys(left: ByteArray, right: ByteArray) {
        var curRound = arrayOfNulls<ByteArray>(2)
        initRoundConsts()
        roundKeys[0] = left
        roundKeys[1] = right
        curRound[0] = left
        curRound[1] = right
        for (i in 0..3) {
            for (j in 0..7) curRound = FeistelRound(
                curRound[0], curRound[1], roundConsts[j + 8 *
                        i]
            )
            roundKeys[2 * i + 2] = curRound[0]
            roundKeys[2 * i + 3] = curRound[1]
        }
    }

    //наложение ключей и шифрование одного блока данных
    fun encrypt(inputBlock: ByteArray): ByteArray {
        var outputBlock = inputBlock
        for (i in 0..8) {
            outputBlock = XOR(outputBlock, roundKeys[i])
            outputBlock = S(outputBlock)
            outputBlock = L(outputBlock)
        }
        outputBlock = XOR(outputBlock, roundKeys[9])
        return outputBlock
    }

    //расшифрование одного блока данных
    fun decrypt(inputBlock: ByteArray): ByteArray {
        var outputBlock = inputBlock
        outputBlock = XOR(outputBlock, roundKeys[9])
        for (i in 8 downTo 0) {
            outputBlock = rL(outputBlock)
            outputBlock = rS(outputBlock)
            outputBlock = XOR(outputBlock, roundKeys[i])
        }
        return outputBlock
    }

    //потоковое шифрование
    fun encryptCTR(input: ByteArray): ByteArray {
        stopCTR = input.size
        val size = blocksCeiling(input)
        val inputBlocks = splitData(input)
        initT(size)
        val O = generateO()
        val outputBlocks = Array(size) {
            ByteArray(
                16
            )
        }
        for (i in 0 until size) outputBlocks[i] = XOR(inputBlocks[i], O[i])
        return concatData(outputBlocks)
    }

    //потоковое расшифрование
    fun decryptCTR(input: ByteArray): ByteArray {
        val size = blocksCeiling(input)
        val inputBlocks = splitData(input)
        val O = generateO()
        val outputBlocks = Array(size) {
            ByteArray(
                16
            )
        }
        for (i in 0 until size) outputBlocks[i] = XOR(inputBlocks[i], O[i])
        val temp = concatData(outputBlocks)
        val output = ByteArray(stopCTR)
        System.arraycopy(temp, 0, output, 0, stopCTR)
        return output
    }

    private fun generateO(): Array<ByteArray> {
        val O = Array(T!!.size) {
            ByteArray(
                16
            )
        }
        var i = 0
        for (t in T!!) O[i++] = encrypt(t)
        return O
    }

    private fun initT(size: Int) {
        T = HashSet(size)
        try {
            while ((T as HashSet<ByteArray>).size != size) {
                val randomBytes = ByteArray(16)
                SecureRandom.getInstance("SHA1PRNG").nextBytes(randomBytes)
                (T as HashSet<ByteArray>).add(randomBytes)
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    private fun splitData(input: ByteArray): Array<ByteArray> {
        val size = blocksCeiling(input)
        val output = Array(size) {
            ByteArray(
                16
            )
        }
        var i = 0
        var j = 0
        var k = 0
        while (k < input.size) {
            if (j == 16) {
                i++
                j = 0
            }
            output[i][j] = input[k]
            k++
            j++
        }
        return output
    }

    //конкатенация строк 𝐴,𝐵∈𝑉^∗
    private fun concatData(input: Array<ByteArray>): ByteArray {
        val output = ByteArray(input.size * BLOCK_SIZE_BYTES)
        var i = 0
        var j = 0
        var k = 0
        while (k < output.size) {
            if (j == 16) {
                i++
                j = 0
            }
            output[k] = input[i][j]
            k++
            j++
        }
        return output
    }

    private fun blocksCeiling(input: ByteArray): Int {
        var result = input.size / BLOCK_SIZE_BYTES
        if (input.size % BLOCK_SIZE_BYTES != 0) result++
        return result
    }

    companion object {
        private var instance: ChipperGost34_12_2015? = null
        fun getInstance(key: ByteArray, iVector: ByteArray): ChipperGost34_12_2015? {
            if (instance == null) instance = ChipperGost34_12_2015(key, iVector)
            return instance
        }
    }
}