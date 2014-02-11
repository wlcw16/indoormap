package com.indoormap.framework.exception;

/**
 * 
 * 系统异常类
 * 
 * @author zhuweiliang
 * @version [版本号, 2012-8-28]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ServiceException extends Exception
{
    public enum ERRORCODE
    {
        NETWROKERROE
    }
    
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;
    
    private ERRORCODE erroeCode;
    
    // 错误ID，编码规则见相应的编码信息表
    private int errorID;
    
    private String exceptionMessage = "";
    
    /**
     * 最基本的例外封装
     * 
     * @param ex 例外错误
     */
    public ServiceException(Exception ex)
    {
        super(ex);
        this.exceptionMessage = ex.getMessage();
        
    }
    
    /**
     * 封装错误号的例外处理
     * 
     * @param id 信息id
     * @param ex 例外错误信息
     */
    public ServiceException(int id, Exception ex)
    {
        super(ex);
        this.exceptionMessage = ex.getMessage();
        this.errorID = id;
    }
    
    /**
     * 封装错误号的例外处理
     * 
     * @param id 信息id
     * @param ex 例外错误信息
     */
    public ServiceException(ERRORCODE id, Exception ex)
    {
        super(ex);
        this.exceptionMessage = ex.getMessage();
        this.erroeCode = id;
    }
    
    /**
     * 封装错误号的例外处理
     * 
     * @param id 信息id
     */
    public ServiceException(int id)
    {
        this.errorID = id;
    }
    
    /**
     * 封装错误号的例外处理 ,并带入变量
     * 
     * @param id 信息id
     * @param exceptionMessage 错误信息
     */
    public ServiceException(ERRORCODE id, String exceptionMessage)
    {
        this.erroeCode = id;
        this.exceptionMessage = exceptionMessage;
    }
    
    /**
     * 封装错误号的例外处理 ,并带入变量
     * 
     * @param id 信息id
     * @param exceptionMessage 错误信息
     */
    public ServiceException(int id, String exceptionMessage)
    {
        this.errorID = id;
        this.exceptionMessage = exceptionMessage;
    }
    
    @Override
    public String getMessage()
    {
        return exceptionMessage;
    }
    
    /**
     * 获取错误ID
     * 
     * @return String 错误ID
     */
    public int getErrorID()
    {
        return this.errorID;
    }
    
  
    
    public ERRORCODE getErroeCode()
    {
        return erroeCode;
    }

    public String getExceptionMessage()
    {
        return exceptionMessage;
    }
    
    public void setExceptionMessage(String exceptionMessage)
    {
        this.exceptionMessage = exceptionMessage;
    }
    
    /**
     * 重写toString
     * 
     * @return Classname + message
     */
    public String toString()
    {
        // TODO Auto-generated method stub
        return super.toString() + this.getExceptionMessage();
    }
    
}
