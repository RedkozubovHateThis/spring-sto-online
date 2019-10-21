package io.swagger.helper;

import io.swagger.response.report.ExternalTestDetail;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ReportHelper {

    public static List<ExternalTestDetail> buildExternalTestDetails(String externalTest) {

        List<ExternalTestDetail> result = new ArrayList<>();
        String regex = "^\\d+_((ID)?(X)?(Y)?(COLOR)?=\\d+)?(SYMBOL=[A-Z]+)?$";

        if ( externalTest == null || externalTest.length() == 0 ) return result;

        for ( String s : externalTest.split( System.lineSeparator() ) ) {

            if ( !s.matches( regex ) ) continue;

            String[] splitValue = s.split("=");
            Integer id = Integer.parseInt( splitValue[0].split("_")[0] );
            ExternalTestDetail currentTestDetail = filterExternalTestDetail( id, result );

            if ( currentTestDetail == null ) {
                currentTestDetail = new ExternalTestDetail();
                currentTestDetail.setId( id );
            }

            if ( s.contains("_ID=") ) {
                result.add( currentTestDetail );
            }
            else if ( s.contains("_X=") ) {
                currentTestDetail.setX( Integer.parseInt( splitValue[1], 10 ) );
            }
            else if ( s.contains("_Y=") ) {
                currentTestDetail.setY( Integer.parseInt( splitValue[1], 10 ) );
            }
            else if ( s.contains("_SYMBOL=") ) {
                currentTestDetail.setSymbol( splitValue[1] );
            }
        }

        return result;

    }

    private static ExternalTestDetail filterExternalTestDetail(Integer id, List<ExternalTestDetail> externalTestDetails) {

        if ( externalTestDetails.size() == 0 ) return null;

        return externalTestDetails
                .stream()
                .filter( externalTestDetail -> externalTestDetail.getId() != null && externalTestDetail.getId().equals( id ) )
                .findAny().orElse( null );

    }

    public static BufferedImage processInspectionImage(BufferedImage old, List<ExternalTestDetail> externalTestDetails) {
        int w = old.getWidth();
        int h = old.getHeight();

        BufferedImage img = new BufferedImage(
                w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(old, 0, 0, w, h, (img1, infoflags, x, y, width, height) -> false);
        g2d.setPaint(Color.black);
        g2d.setFont(new Font("Times New Roman", Font.BOLD, h / 16));

        FontMetrics fm = g2d.getFontMetrics();

        for (ExternalTestDetail externalTestDetail : externalTestDetails) {

            if ( !externalTestDetail.isValid() ) continue;

            String symbol = externalTestDetail.getSymbol();
            int x = externalTestDetail.getX() - fm.stringWidth(symbol);
            int y = externalTestDetail.getY() - fm.getHeight() + fm.getAscent();

            g2d.drawString(symbol, x, y);

        }

        g2d.dispose();

        return img;
    }

    public static BufferedImage drawFuelLevel(Integer fuelLevel) {

        BufferedImage img = new BufferedImage(
                100, 10, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = img.createGraphics();
        g2d.setColor( Color.white );
        g2d.fillRect(0, 0, 100, 10);

        if ( fuelLevel != null && fuelLevel > 0 ) {
            g2d.setColor( new Color(227, 227, 227) );
            g2d.fillRect(0, 0, fuelLevel, 10);
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor( Color.black );
            g2d.drawLine( fuelLevel, 0, fuelLevel, 10 );
        }

        g2d.dispose();

        return img;
    }

}
