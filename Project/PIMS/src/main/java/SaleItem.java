public class SaleItem {

    private int saleItemId;
    private int saleId;
    private int medicineId;
    private int quantitySold;
    private double priceAtSale;

    public SaleItem() {
    }

    public SaleItem(int saleItemId, int saleId,
                    int medicineId, int quantitySold,
                    double priceAtSale) {

        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale;
    }

    public int getSaleItemId() {
        return saleItemId;
    }

    public void setSaleItemId(int saleItemId) {
        this.saleItemId = saleItemId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getPriceAtSale() {
        return priceAtSale;
    }

    public void setPriceAtSale(double priceAtSale) {
        this.priceAtSale = priceAtSale;
    }
}