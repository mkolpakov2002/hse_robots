package ru.hse.control_system_v2.domain.encryption

class Salsa20 {
    private var index = 0
    private val engineState = IntArray(stateSize)
    private val buffer = IntArray(stateSize)
    private val keyStream = ByteArray(stateSize * 4) // expanded state
    private lateinit var workingKey: ByteArray
    private lateinit var workingIV: ByteArray
    private var initialised = false
    private var counter0 = 0
    private var counter1 = 0
    private var counter2 = 0
    fun init(key: ByteArray, iv: ByteArray?) {
        require(!(iv == null || iv.size != 8)) { "Expected 8 bytes of IV" }
        workingKey = key
        workingIV = iv
        setKey(workingKey, workingIV)
    }

    fun crypt(data: ByteArray, position: Int, length: Int): ByteArray {
        val buffer = ByteArray(length)
        crypt(data, position, length, buffer, 0)
        return buffer
    }

    fun crypt(`in`: ByteArray, inOff: Int, len: Int, out: ByteArray, outOff: Int) {
        check(initialised) { "Salsa20 is not initialised" }
        check(inOff + len <= `in`.size) { "input buffer too short" }
        check(outOff + len <= out.size) { "output buffer too short" }
        check(!isOutOfLimit(len)) { "2^70 byte limit per IV would be exceeded; Change IV" }
        for (i in 0 until len) {
            if (index == 0) {
                salsa20WordToByte(engineState, keyStream)
                engineState[8]++
                if (engineState[8] == 0) {
                    engineState[9]++
                }
            }
            out[i + outOff] = (keyStream[index].toInt() xor `in`[i + inOff].toInt()).toByte()
            index = index + 1 and 63
        }
    }

    private fun setKey(keyBytes: ByteArray, ivBytes: ByteArray) {
        workingKey = keyBytes
        workingIV = ivBytes
        index = 0
        resetCounter()
        var offset = 0
        val constants: ByteArray

        // Key
        engineState[1] = getIntFromBytesLittleEndian(workingKey, 0)
        engineState[2] = getIntFromBytesLittleEndian(workingKey, 4)
        engineState[3] = getIntFromBytesLittleEndian(workingKey, 8)
        engineState[4] = getIntFromBytesLittleEndian(workingKey, 12)
        if (workingKey.size == 32) {
            constants = constant32
            offset = 16
        } else {
            constants = constant16
        }
        engineState[11] = getIntFromBytesLittleEndian(workingKey, offset)
        engineState[12] = getIntFromBytesLittleEndian(workingKey, offset + 4)
        engineState[13] = getIntFromBytesLittleEndian(workingKey, offset + 8)
        engineState[14] = getIntFromBytesLittleEndian(workingKey, offset + 12)
        engineState[0] = getIntFromBytesLittleEndian(constants, 0)
        engineState[5] = getIntFromBytesLittleEndian(constants, 4)
        engineState[10] = getIntFromBytesLittleEndian(constants, 8)
        engineState[15] = getIntFromBytesLittleEndian(constants, 12)

        // IV
        engineState[6] = getIntFromBytesLittleEndian(workingIV, 0)
        engineState[7] = getIntFromBytesLittleEndian(workingIV, 4)
        engineState[9] = 0
        engineState[8] = engineState[9]
        initialised = true
    }

    private fun salsa20WordToByte(input: IntArray, output: ByteArray) {
        System.arraycopy(input, 0, buffer, 0, input.size)
        for (i in 0..9) {
            buffer[4] = buffer[4] xor rotateLeft(buffer[0] + buffer[12], 7)
            buffer[8] = buffer[8] xor rotateLeft(buffer[4] + buffer[0], 9)
            buffer[12] = buffer[12] xor rotateLeft(buffer[8] + buffer[4], 13)
            buffer[0] = buffer[0] xor rotateLeft(buffer[12] + buffer[8], 18)
            buffer[9] = buffer[9] xor rotateLeft(buffer[5] + buffer[1], 7)
            buffer[13] = buffer[13] xor rotateLeft(buffer[9] + buffer[5], 9)
            buffer[1] = buffer[1] xor rotateLeft(buffer[13] + buffer[9], 13)
            buffer[5] = buffer[5] xor rotateLeft(buffer[1] + buffer[13], 18)
            buffer[14] = buffer[14] xor rotateLeft(buffer[10] + buffer[6], 7)
            buffer[2] = buffer[2] xor rotateLeft(buffer[14] + buffer[10], 9)
            buffer[6] = buffer[6] xor rotateLeft(buffer[2] + buffer[14], 13)
            buffer[10] = buffer[10] xor rotateLeft(buffer[6] + buffer[2], 18)
            buffer[3] = buffer[3] xor rotateLeft(buffer[15] + buffer[11], 7)
            buffer[7] = buffer[7] xor rotateLeft(buffer[3] + buffer[15], 9)
            buffer[11] = buffer[11] xor rotateLeft(buffer[7] + buffer[3], 13)
            buffer[15] = buffer[15] xor rotateLeft(buffer[11] + buffer[7], 18)
            buffer[1] = buffer[1] xor rotateLeft(buffer[0] + buffer[3], 7)
            buffer[2] = buffer[2] xor rotateLeft(buffer[1] + buffer[0], 9)
            buffer[3] = buffer[3] xor rotateLeft(buffer[2] + buffer[1], 13)
            buffer[0] = buffer[0] xor rotateLeft(buffer[3] + buffer[2], 18)
            buffer[6] = buffer[6] xor rotateLeft(buffer[5] + buffer[4], 7)
            buffer[7] = buffer[7] xor rotateLeft(buffer[6] + buffer[5], 9)
            buffer[4] = buffer[4] xor rotateLeft(buffer[7] + buffer[6], 13)
            buffer[5] = buffer[5] xor rotateLeft(buffer[4] + buffer[7], 18)
            buffer[11] = buffer[11] xor rotateLeft(buffer[10] + buffer[9], 7)
            buffer[8] = buffer[8] xor rotateLeft(buffer[11] + buffer[10], 9)
            buffer[9] = buffer[9] xor rotateLeft(buffer[8] + buffer[11], 13)
            buffer[10] = buffer[10] xor rotateLeft(buffer[9] + buffer[8], 18)
            buffer[12] = buffer[12] xor rotateLeft(buffer[15] + buffer[14], 7)
            buffer[13] = buffer[13] xor rotateLeft(buffer[12] + buffer[15], 9)
            buffer[14] = buffer[14] xor rotateLeft(buffer[13] + buffer[12], 13)
            buffer[15] = buffer[15] xor rotateLeft(buffer[14] + buffer[13], 18)
        }
        var offset = 0
        for (i in 0 until stateSize) {
            intToByteLittle(buffer[i] + input[i], output, offset)
            offset += 4
        }
        for (i in stateSize until buffer.size) {
            intToByteLittle(buffer[i], output, offset)
            offset += 4
        }
    }

    private fun intToByteLittle(x: Int, out: ByteArray, off: Int): ByteArray {
        out[off] = x.toByte()
        out[off + 1] = (x ushr 8).toByte()
        out[off + 2] = (x ushr 16).toByte()
        out[off + 3] = (x ushr 24).toByte()
        return out
    }

    private fun rotateLeft(x: Int, y: Int): Int {
        return x shl y or (x ushr -y)
    }

    private fun getIntFromBytesLittleEndian(x: ByteArray, offset: Int): Int { //little endian order
        return x[offset].toInt() and 255 or
                (x[offset + 1].toInt() and 255 shl 8) or
                (x[offset + 2].toInt() and 255 shl 16) or
                (x[offset + 3].toInt() shl 24)
    }

    private fun resetCounter() {
        counter0 = 0
        counter1 = 0
        counter2 = 0
    }

    private fun isOutOfLimit(len: Int): Boolean {
        if (counter0 >= 0) {
            counter0 += len
        } else {
            counter0 += len
            if (counter0 >= 0) {
                counter1++
                if (counter1 == 0) {
                    counter2++
                    // 2^(32 + 32 + 6)
                    return counter2 and 0x20 != 0
                }
            }
        }
        return false
    }

    companion object {
        private const val stateSize = 16 // 16, 32 bit ints = 64 bytes
        private val constant32 = "expand 32-byte k".toByteArray()
        private val constant16 = "expand 16-byte k".toByteArray()
    }
}