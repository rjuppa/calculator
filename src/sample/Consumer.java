package sample;

import java.text.DecimalFormat;

public class Consumer {

    private String buffer1 = "";
    private String buffer2 = "";
    private String postOperand = "";
    private String lastInput = "";

    private String operation = "";
    private String lastOperand = "";
    private String message = "";

    private Boolean isFinished = false;
    private Boolean showBuffer2 = false;
    private Boolean editBuffer2 = false;
    private Boolean clearing = false;


    public void consume(String s) throws Exception{
        if( isAccepted(s)){

            if( s.equals("=")){         // ----------------- Equal '='
                if( operation.length() == 0 ){
                    message = "";
                }
                else{
                    if(buffer1.length() > 0) {
                        String op2;
                        if(buffer2.length() > 0 ){
                            op2 = buffer2;
                            lastOperand = buffer2;
                        }
                        else{
                            if( lastOperand.length() > 0 ){
                                op2 = lastOperand;
                            }
                            else{
                                op2 = buffer1;
                            }
                        }
                        recalc(buffer1, op2);
                        isFinished = true;
                        editBuffer2 = false;
                        showBuffer2 = false;
                    }
                }
            }

            if( isOp(s)){               // ----------------- Operations +-*/
                isFinished = false;
                if( operation.length() == 0 ) {
                    operation = s;
                    editBuffer2 = true;
                    showBuffer2 = false;
                    message = "";
                }
                else{
                    // second operation
                    if( !clearing && (s.equals("*") || s.equals("/"))
                        && (operation.equals("+") || operation.equals("-")) && buffer2.length() > 0){
                            postOperand = buffer1;
                            buffer1 = buffer2;
                            lastOperand = buffer2;
                            buffer2 = "";
                            editBuffer2 = true;
                            showBuffer2 = clearing;
                            message = "";
                    }
                    else{
                        if( buffer2.length() > 0){
                            lastOperand = buffer2;
                            recalc(buffer1, buffer2);
                            editBuffer2 = true;
                            showBuffer2 = false;
                        }
                        else{
                            editBuffer2 = true;
                            showBuffer2 = clearing;
                            message = "";
                        }
                    }
                    operation = s;
                }
            }

            if( isNum(s) ){             // ----------------- Number 0.123456789
                clearing = false;
                if( isFinished ){
                    buffer1 = "";
                    lastOperand = "";
                    buffer1 = addToBuffer(buffer1, s);   // after '='
                    buffer2 = "";
                    operation = "";
                    editBuffer2 = false;
                    showBuffer2 = false;
                    isFinished = false;
                }
                else{
                    if( operation.length() == 0 ) {
                        editBuffer2 = false;
                        showBuffer2 = false;
                    }
                    if( editBuffer2 ) {
                        buffer2 = addToBuffer(buffer2, s);
                        showBuffer2 = true;
                    }
                    else{
                        buffer1 = addToBuffer(buffer1, s);
                        showBuffer2 = false;
                    }
                }
            }

            if( s.equals("s")){         // ----------------- sign
                if( editBuffer2 ) {
                    buffer2 = signBuffer(buffer2);
                }
                else{
                    buffer1 = signBuffer(buffer1);
                }
            }

            if( s.equals("a")){         // ----------------- clear
                clear();
            }

            if( s.equals("c")){         // ----------------- clear
                clearBuffer();
            }
        }
        else{
            message = "";
        }
        lastInput = s;
    }


    public String getClearLabel() {
        if(buffer2.length() == 0){
            return "AC";
        }
        return "C";
    }

    public String getResult(){
        if( showBuffer2 ){
            return showBuffer(buffer2);
        }
        else{
            return showBuffer(buffer1);
        }
    }

    public String getMessage(){
        return message;
    }

    private void clear() {
        buffer1 = "";
        buffer2 = "";
        lastOperand = "";
        postOperand = "";
        operation = "";
        message = "";
        isFinished = false;
        editBuffer2 = false;
        showBuffer2 = false;
    }

    private void clearBuffer() {
        buffer2 = "";
        isFinished = false;
        editBuffer2 = true;
        showBuffer2 = true;
        clearing = true;
    }

    private void recalc(String b1, String b2) throws Exception {
        double d1 = Double.parseDouble(b1);
        double d2 = Double.parseDouble(b2);
        message =  String.format("%s %s %s", b1, operation, b2);

        // calc =====================
        double d = calc(d1, d2, operation);
        if( postOperand.length() > 0 ){
            double po = Double.parseDouble(postOperand);
            d = calc(d, po, "+");
            message =  String.format("%s + %s", postOperand, message);
            postOperand = "";
        }
        buffer1 = formatBuffer(d);
        buffer2 = "";
        showBuffer2 = false;
        message =  String.format("%s = %s", message, getResult());
    }

    private double calc(double d1, double d2, String op) throws Exception {
        switch (op) {
            case "+": return d1 + d2;
            case "-": return d1 - d2;
            case "*": return d1 * d2;
            case "/":
                if( d2 == 0 ){
                    clear();
                    throw new Exception("Division by zero.");
                }
                return d1 / d2;
        }
        throw new Exception("Not supported.");
    }

    private String showBuffer(String buffer){
        if( buffer.length() == 0 ){
            return "0";
        }
        return String.format("%s", buffer);
    }

    private String formatBuffer(double d){
        long iresult = (long)d;
        if(d > 9999999999999L){
            return String.format("%6.3e", d);
        }
        if(Math.abs(d-iresult) < 0.00000000001){
            // use integers
            return String.format("%d", iresult);
        }
        else{
            // use double
            String s = String.format("%.10f", d);
            if( s.length() > 13){
                String[] parts = s.split("\\.");
                int part1 = parts[0].length();
                if( part1 < 12 ) {
                    String pattern = "#." + "####################".substring(0, 12 - part1);
                    return new DecimalFormat(pattern).format(d);
                }
                s = String.format("%s", d);
                if(s.contains("E")){
                    parts = s.split("E");
                    part1 = parts[0].length();
                    if( part1 > 8 ){            // it is rounded here
                        return parts[0].substring(0, 8) + "e+" + parts[1];
                    }
                }
            }
            return removeZeros(s);
        }
    }

    private String removeZeros(String s){
        int n = s.length();
        String ch;
        for(int i=n-1; i>0; i--){
            ch = s.substring(i, i+1);
            if( !ch.equals("0") ){
                return s.substring(0, i+1);
            }
        }
        return s;
    }

    private Boolean isAccepted(String s){
        // reject invalid input
        if( isOp(s) ){
            if(isOp(lastInput)) {        // after operation must be number
                operation = s;
                isFinished = false;
                return false;
            }
        }

        if( isNum(s) ){
            if( isFinished ){
                return true;
            }
            if( operation.length() == 0 ) {
                if( s.equals(".") && isDecimal(buffer1) ){   // only a single dot is allowed
                    return false;
                }
            }
            else{
                if( s.equals(".") && isDecimal(buffer2) ){   // only a single dot is allowed
                    return false;
                }
            }
        }
        return true;
    }

    private String addToBuffer(String buffer, String s){
        if(buffer.equals("") && s.equals(".")){
            return "0.";
        }
        if(buffer.equals("0") && s.equals(".")){
            return "0.";
        }
        if(buffer.equals("0")){
            return s;
        }
        buffer = buffer + s;
        return buffer;
    }

    private String signBuffer(String buffer){
        if(buffer.startsWith("-")){
            buffer = buffer.substring(1);
        }
        else{
            buffer = "-" + buffer;
        }
        return buffer;
    }

    private Boolean isOp(String s){
        return "+-*/".contains(s);
    }

    private Boolean isNum(String s){
        return "0.123456789".contains(s);
    }

    private Boolean isDecimal(String number){
        return number.contains(".");
    }

}
