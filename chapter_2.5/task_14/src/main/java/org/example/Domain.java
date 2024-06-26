package org.example;

public class Domain implements Comparable<Domain> {

    private String domen;

    public Domain(final String domen) {
        this.domen = domen;
    }

    public void setDomen(final String domen) {
        this.domen = domen;
    }

    public String getDomen() {
        return domen;
    }

    @Override
    public int compareTo(final Domain o) {
        return this.domen.compareTo(o.domen);
    }

    public static String reverseDomen(final String domen) {
        String[] domenArray = domen.split("\\.");
        StringBuilder reverseDomen = new StringBuilder();
        for (int i = domenArray.length - 1; i >= 0; i--) {
            reverseDomen.append(domenArray[i]);
            if (i > 0) {
                reverseDomen.append(".");
            }
        }
        return reverseDomen.toString();
    }
}
