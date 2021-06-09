package com.kamennova.doggies.route;

import com.kamennova.doggies.route.geom.BaseCoordinate;
import com.kamennova.doggies.route.geom.DoubleCoordinate;
import com.kamennova.doggies.route.geom.IntCoordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinatesCoder {
    public static List<IntCoordinate> decode(String compressed, DoubleCoordinate start) {
        final List<IntCoordinate> diffs = stringToCoords(compressed);
        final ArrayList<IntCoordinate> output = new ArrayList<>();

        final BaseCoordinate<Integer> a = start.apply(CoordinatesCoder::getMinAndSec);
        output.add(new IntCoordinate(a.getLat(), a.getLng()));

        diffs.forEach(diff -> {
            final IntCoordinate prev = output.get(output.size() - 1);
            final IntCoordinate coord = new IntCoordinate(prev.getLat() + diff.getLat(), prev.getLng() + diff.getLng());
            output.add(coord);
        });

        return output;
    }

    public static Double getFullFromMinAndSec(Integer minAndSec) {
        // return minAndSec * Math.pow(0.1, 6 + minAndSec.toString().length());
        return Double.parseDouble((minAndSec < 0 ? "-" : "") + "0." + "0".repeat(6 - String.valueOf(Math.abs(minAndSec)).length()) + Math.abs(minAndSec));
    }

    public static Integer getMinAndSec(Double point) {
        return (int) Math.round((point - Math.floor(point)) * 1000000);
    }

    public static String encode(List<DoubleCoordinate> input) {
        final ArrayList<IntCoordinate> output = new ArrayList<>();
        BaseCoordinate<Integer> prevReduced = input.get(0).apply(CoordinatesCoder::getMinAndSec);

        for (int i = 1; i < input.size(); i++) {
            final DoubleCoordinate coordFull = input.get(i);
            final BaseCoordinate<Integer> coordReduced = coordFull.apply(CoordinatesCoder::getMinAndSec);
            final IntCoordinate diff = new IntCoordinate(coordReduced.getLat() - prevReduced.getLat(), coordReduced.getLng() - prevReduced.getLng());

            if (diff.getLat() == 0 && diff.getLng() == 0) { // todo why?
                continue;
            }

            output.add(diff);
            prevReduced = coordReduced;
        }

        return coordsToString(output);
    }

    private static String coordsToString(List<IntCoordinate> coords) {
        return coords.stream().map(c -> c.getLat() + "," + c.getLng()).collect(Collectors.joining(";"));
    }

    private static List<IntCoordinate> stringToCoords(String str) {
        return Arrays.stream(str.split(";")).map(coordStr -> {
            final String[] numStr = coordStr.split(",");
            return new IntCoordinate(Integer.parseInt(numStr[0]), Integer.parseInt(numStr[1]));
        }).collect(Collectors.toList());
    }
}
