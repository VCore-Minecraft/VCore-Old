/*
 * Copyright (c) 2021. Lukas Jonsson
 */

package de.verdox.vcore.util.global;

import de.verdox.vcore.util.VCoreUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    public double randomDouble(double min, double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public double randomPercentage() {
        return VCoreUtil.getMathUtil().roundToTwoDigits(randomDouble(0, 100));
    }

    public int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static class ProbabilityCounter<T> {
        Map<T, Integer> counting = new HashMap<>();

        public void count(T element) {
            if (!counting.containsKey(element))
                counting.put(element, 1);
            else
                counting.put(element, counting.get(element) + 1);
        }

        public Map<T, Integer> getCounting() {
            return counting;
        }
    }

    public static class RandomCollection<T> {
        private final NavigableMap<Double, T> map = new TreeMap<>();
        private final List<Map.Entry<T, Double>> elementWeightList = new ArrayList<>();
        private double total = 0;

        public void add(double weight, T result) {
            if (weight <= 0 || map.containsValue(result))
                return;
            total += weight;
            map.put(total, result);
            elementWeightList.add(Map.entry(result, weight));
        }

        public T next() {
            double value = ThreadLocalRandom.current().nextDouble() * total;
            if (map.isEmpty())
                throw new NoSuchElementException("RandomCollection is empty!");
            return map.ceilingEntry(value).getValue();
        }

        public void addAll(RandomCollection<T> randomCollection) {
            randomCollection.elementWeightList.iterator().forEachRemaining(tDoubleEntry -> add(tDoubleEntry.getValue(), tDoubleEntry.getKey()));
        }

        public List<Map.Entry<T, Double>> getElementWeightList() {
            return elementWeightList;
        }

        public Map<T, Integer> benchmark(int number, boolean print) {
            Map<T, Integer> benchmark = new HashMap<>();

            for (int i = 0; i < number; i++) {
                T object = next();
                if (!benchmark.containsKey(object))
                    benchmark.put(object, 1);
                else
                    benchmark.put(object, benchmark.get(object) + 1);
            }
            if (print) {
                benchmark.forEach((object, integer) -> {
                    System.out.println(object + " : " + integer);
                });
            }
            return benchmark;
        }

        public NavigableMap<Double, T> getMap() {
            return map;
        }
    }
}
