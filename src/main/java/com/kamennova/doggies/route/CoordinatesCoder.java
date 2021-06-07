package com.kamennova.doggies.route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CoordinatesCoder {
    public static List<Coordinate> decode(String compressed, Coordinate start) {
        List<Integer> dist = Arrays.stream(compressed.split(",")).map(Integer::parseInt).collect(Collectors.toList());

        List<Coordinate> coords = new ArrayList<>();
        coords.add(start);

        for (int i = 0; i < dist.size() / 2; i++) {
            final Coordinate prev = coords.get(coords.size() - 1);
            final BaseCoordinate<Double> diff = new BaseCoordinate<Integer>(dist.get(i * 2), dist.get(i * 2 + 1)).apply(CoordinatesCoder::getFullFromMinAndSec);
            final Coordinate coord = new Coordinate(prev.getLat() + diff.getLat(), prev.getLng() + diff.getLng());
            coords.add(coord);
        }

        return coords;
    }

    private static Double getFullFromMinAndSec(Integer minAndSec) {
//        return minAndSec * Math.pow(0.1, 6 + minAndSec.toString().length());
        return Double.parseDouble((minAndSec < 0 ? "-" : "") + "0." + "0".repeat(6 - String.valueOf(Math.abs(minAndSec)).length()) + Math.abs(minAndSec));
    }

    private static Integer getMinAndSec(Double point) {
        return (int) Math.round((point - Math.floor(point)) * 1000000);
    }

    public static String encode(List<Coordinate> input) {
        final ArrayList<Integer> output = new ArrayList<>();

        BaseCoordinate<Integer> prevReduced = input.get(0).apply(CoordinatesCoder::getMinAndSec);

        for (int i = 1; i < input.size(); i++) {
            final Coordinate coordFull = input.get(i);
            final BaseCoordinate<Integer> coordReduced = coordFull.apply(CoordinatesCoder::getMinAndSec);

            output.add(coordReduced.getLat() - prevReduced.getLat());
            output.add(coordReduced.getLng() - prevReduced.getLng());

            prevReduced = coordReduced;
        }

        return output.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
