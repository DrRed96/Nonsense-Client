package optifine;

import java.util.Map;

public class FileUploadThread extends Thread
{
    private String urlString;
    private Map<String, String> headers;
    private byte[] content;
    private IFileUploadListener listener;

    public FileUploadThread(String url, Map<String, String> headers, byte[] content, IFileUploadListener listener)
    {
        this.urlString = url;
        this.headers = headers;
        this.content = content;
        this.listener = listener;
    }

    public void run()
    {
        try
        {
            HttpUtils.post(this.urlString, this.headers, this.content);
            this.listener.fileUploadFinished(this.urlString, this.content, null);
        }
        catch (Exception exception)
        {
            this.listener.fileUploadFinished(this.urlString, this.content, exception);
        }
    }

    public String getUrlString()
    {
        return this.urlString;
    }

    public byte[] getContent()
    {
        return this.content;
    }

    public IFileUploadListener getListener()
    {
        return this.listener;
    }
}
