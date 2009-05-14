package com.gentics.cr.rest.php;

/*
 * PHPSerializable.java
 *
 * Author : Ludovic Martin <ludovic DOT martin AT laposte DOT net>
 * This code is free for any use and modification but comes without any warranty.
 */

/**
 * Interface for "PHP serializable objects"
 * @author Ludovic
 */
public interface PHPSerializable{
    
    /**
     * Called by PHPSerializer when the objet is serialized
     * @return serialized data
     */
    public String phpSerialize();
}