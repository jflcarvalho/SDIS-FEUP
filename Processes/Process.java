public class Process {
    public static void main(String[] args){
        System.out.println("Start!");
        System.out.print("WORKING [");
        for(int i = 0; i < 50; i++) {
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("=");
        }
        System.out.println("]");
        System.out.println("All Work Done!");
    }
}