package team.travel.travelplanner.ndfd.degrib.simple;

import team.travel.travelplanner.ndfd.degrib.WeatherCoverage;
import team.travel.travelplanner.ndfd.degrib.WeatherIntensity;
import team.travel.travelplanner.ndfd.degrib.WeatherType;
import team.travel.travelplanner.ndfd.degrib.WeatherWord;

import java.util.List;

import static team.travel.travelplanner.ndfd.degrib.WeatherCoverage.*;
import static team.travel.travelplanner.ndfd.degrib.WeatherIntensity.*;
import static team.travel.travelplanner.ndfd.degrib.WeatherType.*;

/**
 * Adapted to Java from degrib/src/degrib/weather.c
 */
public class SimpleWeatherCode4 {

    /*****************************************************************************
     * NDFD_Wx2Code4() --
     *
     * Original: wx2code() Mark Armstrong Nov 2004 (MDL)
     * Adapted to NDFD_Wx2Code() Arthur Taylor (MDL)
     *
     * PURPOSE
     *   Converts from a Weather Type to the type value used in makeWxImageCodes.
     *
     * ARGUMENTS
     * wxtype = The weather type to encode. (Input)
     *
     * FILES/DATABASES: None
     *
     * RETURNS: int (the encoded number.)
     *
     * HISTORY
     *  11/2004 Mark Armstrong (MDL): Created matching algorithm "wx2code"
     *  11/2004 Arthur Taylor (MDL): Modified to assist with NDFD_WxTable4()
     *
     * NOTES
     *****************************************************************************
     */
    private static int NDFD_Wx2Code4(WeatherType code) {
        return switch (code) {
            case WX_RW -> 10;
            case WX_L -> 20;
            case WX_ZL -> 30;
            case WX_ZR -> 40;
            case WX_IP -> 50;
            case WX_SW -> 60;
            case WX_S -> 70;
            case WX_T -> 80;
            case WX_F -> 90;
            default -> 0;
        };
    }

    /*****************************************************************************
     * NDFD_CodeIntens4() --
     *
     * Original: code_intensity() Mark Armstrong Nov 2004 (MDL)
     * Adapted to NDFD_CodeIntens4() Arthur Taylor (MDL)
     *
     * PURPOSE
     *   Converts from two types of weather intensities to the type value used in
     * makeWxImageCodes when dealing with intensities.
     *
     * ARGUMENTS
     * inten1 = The first intensity to encode. (Input)
     * inten2 = The second intensity to encode. (Input)
     *
     * FILES/DATABASES: None
     *
     * RETURNS: int (the encoded number.)
     *
     * HISTORY
     *  11/2004 Mark Armstrong (MDL): Created matching algorithm "code_intensity"
     *  11/2004 Arthur Taylor (MDL): Modified to assist with NDFD_WxTable4()
     *
     * NOTES
     *****************************************************************************
     */
    private static int NDFD_CodeIntens4(WeatherIntensity inten1, WeatherIntensity inten2) {
        return switch (inten2) {
            case INT_NOINT, INT_UNKNOWN, INT_M -> switch (inten1) {
                case INT_NOINT, INT_UNKNOWN, INT_M -> 0;
                case INT_D, INT_DD -> 1;
                default -> 2;
            };
            case INT_D, INT_DD -> switch (inten1) {
                case INT_NOINT, INT_UNKNOWN, INT_M -> 3;
                case INT_D, INT_DD -> 4;
                default -> 5;
            };
            default -> switch (inten1) {
                case INT_NOINT, INT_UNKNOWN, INT_M -> 6;
                case INT_D, INT_DD -> 7;
                default -> 8;
            };
        };
    }

    /*****************************************************************************
     * NDFD_WxTable4() --
     *
     * Original: makeWxImageCodes() Mark Armstrong Nov 2004 (MDL)
     * Adapted to NDFD_WxTable4() Arthur Taylor (MDL)
     *
     * PURPOSE
     *   To use the same weather table scheme used by Mark Armstrong in
     * makeWxImageCodes().  The purpose of both procedures is to simplify the
     * weather string (aka ugly string) to a single integral type number, which
     * contains the most releavent weather.  The intent is to create a simpler
     * field which can more readily be viewed as an image.
     *
     * ARGUMENTS
     * ugly = The ugly weather string to encode. (Input)
     *
     * FILES/DATABASES: None
     *
     * RETURNS: int (the encoded number.)
     *
     * HISTORY
     *  11/2004 Mark Armstrong (MDL): Created "makeWxImageCodes"
     *  11/2004 Arthur Taylor (MDL): Created NDFD_WxTable4
     *
     * NOTES
     *  1) The table used... In the past I have included the table as part of the
     * documentation here, but since the table is now > 1000 lines long, I think
     * it best to look in "/degrib/data/imageGen/colortable/Wx_200411.colortable"
     *****************************************************************************
     */
    public static int NDFD_WxTable4(List<WeatherWord> words) {
        int numValid = words.size();
        WeatherCoverage cover1 = COV_NOCOV;
        WeatherIntensity intens1 = INT_NOINT;
        WeatherWord word0 = words.get(0);
        WeatherWord word1 = numValid > 1 ? words.get(1) : null;

        if (numValid > 1) {
            cover1 = word1.coverage();
            intens1 = word1.intensity();
            if ((word1.type() != WX_R) && (word1.type() != WX_S) && (word1.type() != WX_RW) &&
                    (word1.type() != WX_SW) && (word1.type() != WX_T) && (word1.type() != WX_ZR) &&
                    (word1.type() != WX_IP) && (word1.type() != WX_ZL) && (word1.type() != WX_L) &&
                    (word1.type() != WX_F)) {
                numValid = 1;
                cover1 = COV_UNKNOWN;
                intens1 = INT_UNKNOWN;
            }
        }

        int code;
        switch (word0.type()) {
            case WX_NOWX:    /* NoWx */
            case WX_A:       /* Hail */
            case WX_FR:      /* Frost */
                code = 0;
                break;
            case WX_R:       /* Rain */
                code = 1;
                if (numValid > 1) {
                    code = 100 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_RW:      /* Rain Showers */
                code = 4;
                if (numValid > 1) {
                    code = 200 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_L:       /* Drizzle */
                code = 7;
                if (numValid > 1) {
                    code = 300 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_ZL:      /* Freezing Drizzle */
                code = 10;
                if (numValid > 1) {
                    code = 400 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_ZR:      /* Freezing Rain */
                code = 13;
                if (numValid > 1) {
                    code = 500 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_IP:      /* Sleet */
                code = 16;
                if (numValid > 1) {
                    code = 600 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_SW:      /* Snow Showers */
                code = 19;
                if (numValid > 1) {
                    code = 700 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_S:       /* Snow */
                code = 22;
                if (numValid > 1) {
                    code = 800 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_T:       /* Thunderstorms */
                code = 25;
                if (numValid > 1) {
                    code = 900 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_F:       /* Fog */
                code = 28;
                if (numValid > 1) {
                    code = 1000 + NDFD_Wx2Code4(word1.type());
                }
                break;
            case WX_K:       /* Smoke */
                code = 31;
                break;
            case WX_BS:      /* Blowing Snow */
                code = 32;
                break;
            case WX_BD:      /* Blowing Dust */
                code = 33;
                break;
            case WX_ZF:      /* Freezing Fog */
                code = 34;
                break;
            case WX_IF:      /* Ice Fog */
                code = 35;
                break;
            case WX_IC:      /* Ice Crystals */
                code = 36;
                break;
            case WX_BN:      /* Blowing Sand */
                code = 37;
                break;
            case WX_ZY:      /* Freezing Spray */
                code = 38;
                break;
            case WX_VA:      /* Volcanic Ash */
                code = 39;
                break;
            case WX_WP:      /* Water Spouts */
                code = 40;
                break;
            case WX_H:       /* Haze */
                code = 41;
                break;
            default:
                code = 0;
        }                    /* End of Switch statement. */

        switch (word0.type()) {
            case WX_R, WX_S, WX_RW, WX_SW, WX_T, WX_ZR, WX_IP, WX_ZL, WX_L, WX_F ->
                    code += NDFD_CodeIntens4(word0.intensity(), intens1);
        }

        switch (word0.coverage()) {
            case COV_WIDE, COV_LKLY, COV_NUM, COV_OCNL, COV_DEF, COV_AREAS, COV_PDS, COV_FRQ, COV_INTER, COV_BRIEF ->
                    code += 1100;
            default -> {
                switch (cover1) {
                    case COV_WIDE, COV_LKLY, COV_NUM, COV_OCNL, COV_DEF, COV_AREAS, COV_PDS, COV_FRQ, COV_INTER, COV_BRIEF ->
                            code += 1100;
                }
            }
        }

        return code;
    }
}
