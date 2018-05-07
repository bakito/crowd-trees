package ch.bakito.crowd.constants;

public enum ImageType {
  svg(Constants.IMAGE_SVG_XML, "", "svg"), png(Constants.IMAGE_PNG, "", "png"), jpg(Constants.IMAGE_JPG, "", "jpg"), dot(Constants.TEXT_PLAIN, Constants.CHARSET_UTF_8, "dot");

  private final String mineType;
  private final String mineTypeSuffix;
  private final String extension;

  ImageType(String mineType, String mineTypeSuffix, String extension) {
    this.mineType = mineType;
    this.mineTypeSuffix = mineTypeSuffix;
    this.extension = extension;
  }

  public static final String ACCEPT = Constants.IMAGE_SVG_XML + ", " + Constants.IMAGE_PNG + ", " + Constants.IMAGE_JPG + ", " + Constants.TEXT_PLAIN;

  public String getMineType() {
    return mineType;
  }

  public String getResponseContentType() {
    return mineType + mineTypeSuffix;
  }

  public String getExtension() {
    return extension;
  }

  private static class Constants {

    private static final String IMAGE_SVG_XML = "image/svg+xml";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_JPG = "image/jpg";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String CHARSET_UTF_8 = "; charset=utf-8";
  }
}
