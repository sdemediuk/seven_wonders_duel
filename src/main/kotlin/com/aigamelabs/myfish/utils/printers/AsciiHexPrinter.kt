package com.aigamelabs.myfish.utils.printers

import com.aigamelabs.myfish.utils.HexOrientation

abstract class AsciiHexPrinter {


    /**
     * Returns the orientation of hexes from the given HexPrinter
     */
    //abstract val hexOrientation: HexOrientation

    /**
     * Returns the hex
     */
    abstract fun getHex(line1: String, line2: String, line3: String, fillerChar: Char): String

    /**
     * Viewing the board as a grid of hexes. Each hex has a bounding box. Map top-left of bounding box given by hex
     * coordinates to same area viewed as char grid.
     *
     * @returns A int[2] with (x,y) char coordinates. (top,left) is (0,0)
     */
    abstract fun mapHexCoordsToCharCoords(q: Int, r: Int): IntArray

    /**
     * Returns the bounding box in chars for a map of the given size
     *
     * @param hexWidth  Size of board in hexes horisontally.
     * @param hexHeight Size of board in hexes verticall.
     * @return A int[2]: int[0] gives the width in chars and int[1] gives the height.
     */
    abstract fun getMapSizeInChars(hexWidth: Int, hexHeight: Int): IntArray

    abstract fun getHexOrientation(): HexOrientation
    /**
     * Makes sure that a string has the given length, using " " (whitespace) if input string is shorter.
     */
    protected fun restrictToLength(str: String?, length: Int): String {
        var result = "  "
        if (str != null) {
            result = if (str.length > length) {
                str.toUpperCase().substring(0, length)
            } else if (str.length < length) {
                pad(str.toUpperCase(), length - str.length)
            } else {
                str
            }
        }

        return result
    }

    /**
     * Pads whitespace to both sides, effectively centering the text.
     * Padding starts at the left side
     * @param s
     * @param n
     * @return
     */
    private fun pad(s: String, n: Int): String {
        var s = s
        var n = n
        while (n > 0) {
            s = if (n % 2 == 0) {
                " $s"
            } else {
                "$s "
            }
            n--
        }

        return s
    }
}
