package ru.ifmo.orthant;

public final class ParameterParser {
    private ParameterParser() {}

    private static final String DC = "OrthantDivideConquer";
    private static final String DC_THREAD_PREFIX = DC + "@";
    private static final String DC_THRESHOLD = DC + "Threshold";
    private static final String DC_THRESHOLD_THREAD_PREFIX = DC_THRESHOLD + "@";

    public static OrthantSearch parseUsedOrthantAlgorithm(String id, int n, int d) {
        if (!id.startsWith("Orthant")) {
            throw new IllegalArgumentException("Unsupported non-'Orthant*' id: '" + id + "'");
        }
        if (id.equals("OrthantNaive")) {
            return new NaiveOrthantSearch(n, d);
        }
        if (id.startsWith(DC)) {
            if (id.startsWith(DC_THRESHOLD)) {
                if (id.equals(DC_THRESHOLD)) {
                    return new DivideConquerOrthantSearch(n, d, true, 1);
                } else if (id.startsWith(DC_THRESHOLD_THREAD_PREFIX)) {
                    try {
                        int nThreads = Integer.parseInt(id.substring(DC_THRESHOLD_THREAD_PREFIX.length()));
                        return new DivideConquerOrthantSearch(n, d, true, nThreads);
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                if (id.equals(DC)) {
                    return new DivideConquerOrthantSearch(n, d, false, 1);
                } else if (id.startsWith(DC_THREAD_PREFIX)) {
                    try {
                        int nThreads = Integer.parseInt(id.substring(DC_THREAD_PREFIX.length()));
                        return new DivideConquerOrthantSearch(n, d, false, nThreads);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        throw new IllegalArgumentException("Unknown id: '" + id + "'");
    }
}
