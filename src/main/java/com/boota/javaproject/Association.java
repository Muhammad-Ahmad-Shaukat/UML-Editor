package com.boota.javaproject;


    Multiplicity startMultiplicity;
    Multiplicity endMultiplicity;
    String text;


        this.startMultiplicity = startMultiplicity;
        this.endMultiplicity = endMultiplicity;
        this.text = text;
    }

    public Multiplicity getStartMultiplicity() {
        return startMultiplicity;
    }

    public void setStartMultiplicity(Multiplicity startMultiplicity) {
        this.startMultiplicity = startMultiplicity;
    }

    public Multiplicity getEndMultiplicity() {
        return endMultiplicity;
    }

    public void setEndMultiplicity(Multiplicity endMultiplicity) {
        this.endMultiplicity = endMultiplicity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
