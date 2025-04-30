package com.backend.roomie.models;

/**
 * This class is an alias for PropertyList to maintain backward compatibility.
 * It extends PropertyList and inherits all its properties and methods.
 * This allows the backend to accept requests using either "PropretyList" or "PropertyList".
 */
public class PropretyList extends PropertyList {
    // No additional fields or methods needed
    // This class simply inherits everything from PropertyList
}