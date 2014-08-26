/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * EyeXStates.h
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_STATES_API__H__)
#define __TOBII_TX_STATES_API__H__

/*********************************************************************************************************************/

/**
  txGetStateAsync

  Gets a state from the server. The state will be delivered as a TX_HANDLE to a state bag. The handle will be
  TX_EMPTY_HANDLE if the requested state was not found.
 
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context from which to get the state.
    Must not be TX_EMPTY_HANDLE.
    
  @param statePath [in]: 
    A string that specifies which the path of the state to get.    
    Must not start with, end with or have two consecutive dots (.).
    Must not be NULL or empty string.

  @param completionHandler [in]: 
    The TX_ASYNCDATACALLBACK that will be invoked when the result have arrived from the server.
    Must not be NULL.

	The data provided by the TX_ASYNCDATACALLBACK will contain a result code which can be retrieved using 
	txGetAsyncDataResult(). The result code will be one of the follwing:

		TX_RESULT_OK: 
			The state was succesfully retrieved.

		TX_RESULT_NOTFOUND:
			The state was not found.

		TX_RESULT_CANCELLED:
			The asynchronous operation was cancelled.

  @param userParam [in]:
    A TX_USERPARAM which will be provided as a parameter to the completion callback. 
    Can be NULL.    
 
  @return 
    TX_RESULT_OK: The state request was successfully sent to the server.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetStateAsync(
    TX_CONTEXTHANDLE hContext,
    TX_CONSTSTRING statePath,
    TX_ASYNCDATACALLBACK completionHandler,
    TX_USERPARAM userParam
    );
TX_C_END

/*********************************************************************************************************************/

#if defined(__cplusplus)
#ifndef TOBII_TX_INTEROP
#include <functional>

    TX_API_FUNCTION_CPP(GetStateAsync,(
        TX_CONTEXTHANDLE hContext,
        TX_CONSTSTRING statePath,
        const Tx::AsyncDataCallback& completionHandler));

#endif
#endif

/*********************************************************************************************************************/

/**
  txGetState

  Gets a state from the server synchronously.
  This method will block until the state has been retrieved or until the operation has failed.
 
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context from which to get the state.
    Must not be TX_EMPTY_HANDLE.
    
  @param statePath [in]: 
    A string that specifies which the path of the state to get.  
    Must not start with, end with or have two consecutive dots (.).
    Must not be NULL or empty string.

  @param phStateBag [out]: 
    A pointer TX_HANDLE which will be set to the state bag.
	Will be set to TX_EMPTY_HANDLE if not found.

  @param userParam [in]:
    A TX_USERPARAM which will be provided as a parameter to the completion callback. 
    Can be NULL.    
 
  @return 
    TX_RESULT_OK: The state was succcessfully retrieved.
	TX_RESULT_NOTFOUND: The state was not found.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function. 
	TX_RESULT_CANCELLED: The operation was cancelled.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetState(
    TX_CONTEXTHANDLE hContext,
    TX_CONSTSTRING statePath,
    TX_HANDLE* phStateBag
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetStateAsync

  Sets a state on the server.
 
  @param hStateBag [in]:
    A handle to the state bag which contains the path and data to set.

  @param completionHandler [in]: 
    The TX_ASYNCDATACALLBACK that will be invoked when the result have arrived from the server.    
    Can be NULL to ignore the result.    

	The data provided by the TX_ASYNCDATACALLBACK will contain a result code which can be retrieved using 
	txGetAsyncDataResult(). The result code will be one of the follwing:

		TX_RESULT_OK: 
			The state was succesfully set.
			
		TX_RESULT_CANCELLED:
			The asynchronous operation was cancelled.

  @param userParam [in]:
    A TX_USERPARAM which will be provided as a parameter to the completion callback. 
    Can be NULL.    
 
  @return 
    TX_RESULT_OK: The set state request was successfully sent to the server.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function. 
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetStateAsync(
    TX_HANDLE hStateBag,
    TX_ASYNCDATACALLBACK completionHandler,
    TX_USERPARAM userParam
    );
TX_C_END

/*********************************************************************************************************************/

#if defined(__cplusplus)
#ifndef TOBII_TX_INTEROP
#include <functional>

TX_API_FUNCTION_CPP(SetStateAsync, (
    TX_HANDLE hStateBag,        
    const Tx::AsyncDataCallback& completionHandler));

#endif
#endif

/*********************************************************************************************************************/

/**
  txCreateStateBag

  Creates a state bag.
 
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to create the state bag.
    Must not be TX_EMPTY_HANDLE.
  
  @param phStateBag [out]: 
    A pointer to a TX_HANDLE which will be set to the newly created state bag.
    This handle must be released using txReleaseObject to avoid leaks.
    Must not be NULL.
    The value of the pointer must be set to TX_EMPTY_HANDLE.
  
  @param statePath [in]: 
    A string that specifies which path this state bag represents.    
    Must not start with, end with or have two consecutive dots (.).
    Must not be NULL or empty string.
 
  @return 
    TX_RESULT_OK: The state bag was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.    
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateStateBag(
    TX_CONTEXTHANDLE hContext,
    TX_HANDLE* phStateBag,
    TX_CONSTSTRING statePath
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetStateBagPath

  Gets the path that a state bag represents.
 
  @param hStateBag [in]: 
    A TX_CONSTHANDLE to the state bag.
    Must not be TX_EMPTY_HANDLE.
  
  @param pStatePath [out]: 
    A TX_STRING to which the state path will be copied.
    Must be at least the size of the value.
    Can be NULL to only get the size of the path.
 
  @param pStatePathSize [in,out]: 
    A pointer to a TX_SIZE which will be set to the size of the state path.
    Must not be NULL.
    The value must be 0 if pStatePath is NULL.
  
  @return 
    TX_RESULT_OK: The path of the state bag or the required size of the string was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBUFFERSIZE: The size of pStatePath is invalid (*pStatePathSize will be set to the required size). 
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetStateBagPath(
    TX_CONSTHANDLE hStateBag,    
    TX_STRING pStatePath,
    TX_SIZE* pStatePathSize
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txRegisterStateObserverAsync

  Registers observation of a specified state path.
  If connection to the server is currently not present this registration will be stored and applied once connection
  has been established. The registration will also be reapplied if the connection is dropped and restablished.
  Multiple registrations of the same state path will be ignored.
 
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to register the state observation.
    Must not be TX_EMPTY_HANDLE.
 
  @param statePath [in]: 
    The state path as a TX_CONSTSTRING.
    Must not start with, end with or have two consecutive dots (.).
    Must not be NULL or empty string.
 
  @return 
    TX_RESULT_OK: The state path was successfully registered.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.    
    TX_RESULT_DUPLICATESTATEOBSERVER: A registration for this path already exists.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txRegisterStateObserver(
    TX_CONTEXTHANDLE hContext,
    TX_CONSTSTRING statePath
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txUnregisterStateObserverAsync

  Unregisters observation of a specified state path.
   
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to unregister the state observation.
    Must not be TX_EMPTY_HANDLE.
 
  @param statePath [in]: 
    The state path as a TX_CONSTSTRING.
    Must not start with, end with or have two consecutive dots (.).
    Must not be NULL or empty string.
 
  @return 
    TX_RESULT_OK: The state path was successfully unregistered.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOT_FOUND: The state path was not observed.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txUnregisterStateObserver(
    TX_CONTEXTHANDLE hContext,
    TX_CONSTSTRING statePath
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetStateValueAsInteger    

  Gets a value from a state bag as a TX_INTEGER.
  If a state value can not be found on the specified path or the value is of another type this call will fail.
 
  @param hStateBag [in]: 
    A TX_CONSTHANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath, [in]: 
    The path to the value.
 
  @param pIntValue [out]: 
    A pointer to a TX_INTEGER which will be set to the value.
 
  @return 
    TX_RESULT_OK: The state value was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: The value was not found.
    TX_RESULT_INVALIDPROPERTYTYPE: The value type was not TX_PROPERTYVALUETYPE_INTEGER.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetStateValueAsInteger(
    TX_CONSTHANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_INTEGER* pIntValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetStateValueAsReal    

  Gets a value from a state bag as a TX_REAL.
  If a state value can not be found on the specified path or the value is of another type this call will fail.
 
  @param hStateBag [in]: 
    A TX_CONSTHANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath [in]: 
    The path to the value.
 
  @param pRealValue [out]: 
    A pointer to a TX_REAL which will be set to the value.
 
  @return 
    TX_RESULT_OK: The state value was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: The value was not found.
    TX_RESULT_INVALIDPROPERTYTYPE: The value type was not TX_PROPERTYVALUETYPE_REAL.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetStateValueAsReal(
    TX_CONSTHANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_REAL* pRealValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetStateValueAsString    

  Gets a value from a state bag as a TX_STRING.
  If a state value can not be found on the specified path or the value is of another type this call will fail.
 
  @param hStateBag [in]: 
    A TX_CONSTHANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath [in]: 
    The path to the value.

  @param pStringValue [out]: 
    A TX_STRING to which the state value will be copied.
    Must be at least the size of the value.
    Can be NULL to only get the size of the value.
 
  @param pStringSize [in,out]: 
    A pointer to a TX_SIZE which will be set to the size of the state value.
    Must not be NULL.
    The value must be 0 if pStringValue is NULL.
 
  @return 
    TX_RESULT_OK: The state value or the required size of the string was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_INVALIDBUFFERSIZE: The size of pStringValue is invalid (*pStringSize will be set to the required size). 
    TX_RESULT_NOTFOUND: The value was not found.
    TX_RESULT_INVALIDPROPERTYTYPE: The value type was not TX_PROPERTYVALUETYPE_STRING.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetStateValueAsString(
    TX_CONSTHANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_STRING pStringValue,    
    TX_SIZE* pStringSize
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetStateValueAsRectangle    

  Gets a value from a state bag as a TX_RECT.
  If a state value can not be found on the specified path or the value is of another type this call will fail.
 
  @param hStateBag [in]: 
    A TX_CONSTHANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath [in]: 
    The path to the value.
 
  @param pRectangle [out]: 
    A pointer to a TX_RECT which will have its members set.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The state value was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: The value was not found.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetStateValueAsRectangle(
    TX_CONSTHANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_RECT* pRectValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetStateValueAsVector2    

  Gets a value from a state bag as a TX_VEC2.
  If a state value can not be found on the specified path or the value is of another type this call will fail.
 
  @param hStateBag [in]: 
    A TX_CONSTHANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath [in]: 
    The path to the value.
 
  @param pVector2Value [out]: 
    A pointer to a TX_VEC2 which will have its members set.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The state value was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: The value was not found.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetStateValueAsVector2(
    TX_CONSTHANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_VEC2* pVector2Value
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetStateValueAsSize2

  Gets a value from a state bag as a TX_SIZE2.
  If a state value can not be found on the specified path or the value is of another type this call will fail.
 
  @param hStateBag [in]: 
    A TX_CONSTHANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath [in]: 
    The path to the value.
 
  @param pSizeValue [out]: 
    A pointer to a TX_SIZE2 which will have its members set.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The state value was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: The value was not found.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetStateValueAsSize2(
    TX_CONSTHANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_SIZE2* pSizeValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetStateValueAsInteger    

  Sets a value in a state bag to a TX_INTEGER.
 
  @param hStateBag [in]: 
    A TX_HANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath, [in]: 
    The path to the value.
 
  @param intValue [in]: 
    A TX_INTEGER which is the value to set.
 
  @return 
    TX_RESULT_OK: The state value was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetStateValueAsInteger(
    TX_HANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_INTEGER intValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetStateValueAsReal    

  Sets a value in a state bag to a TX_REAL.
 
  @param hStateBag [in]: 
    A TX_HANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath, [in]: 
    The path to the value.
 
  @param intValue [in]: 
    A TX_REAL which is the value to set.
 
  @return 
    TX_RESULT_OK: The state value was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetStateValueAsReal(
    TX_HANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_REAL realValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetStateValueAsString    

  Sets a value in a state bag to a string.
 
  @param hStateBag [in]: 
    A TX_HANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath, [in]: 
    The path to the value.
 
  @param stringValue [in]: 
    A TX_CONSTSTRING which is the value to set.
 
  @return 
    TX_RESULT_OK: The state value was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetStateValueAsString(
    TX_HANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    TX_CONSTSTRING stringValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetStateValueAsRectangle    

  Sets a value in a state bag to a TX_RECT.
 
  @param hStateBag [in]: 
    A TX_HANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath, [in]: 
    The path to the value.
 
  @param pRectValue [in]: 
    A pointer to a TX_RECT which is the value to set.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The state value was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetStateValueAsRectangle(
    TX_HANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    const TX_RECT* pRectValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetStateValueAsVector2    

  Sets a value in a state bag to a TX_VEC2.
 
  @param hStateBag [in]: 
    A TX_HANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath, [in]: 
    The path to the value.
 
  @param pVector2Value [in]: 
    A pointer to a TX_VEC2 which is the value to set.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The state value was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetStateValueAsVector2(
    TX_HANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    const TX_VEC2* pVector2Value
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txSetStateValueAsSize2

  Sets a value in a state bag to a TX_SIZE2.
 
  @param hStateBag [in]: 
    A TX_HANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
        
  @param valuePath, [in]: 
    The path to the value.
 
  @param pSizeValue [in]: 
    A pointer to a TX_SIZE2 which is the value to set.
	Must not be NULL.
 
  @return 
    TX_RESULT_OK: The state value was successfully set.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txSetStateValueAsSize2(
    TX_HANDLE hStateBag,    
    TX_CONSTSTRING valuePath,
    const TX_SIZE2* pSizeValue
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txGetPropertyForStateValue

  Gets a property for a specified state value.
  If the property for state value can not be found on the specified path or the value is of another type this call
  will fail.

  This function is typically only used by bindings.
 
  @param hStateBag [in]: 
    A TX_CONSTHANDLE to the state bag from which the value should be retrieved.
    Must not be TX_EMPTY_HANDLE.
 
  @param phProperty [out]: 
    A pointer to a TX_PROPERTYHANDLE which will be set to the property for the state value.

  @param valuePath [in]: 
    The path to the value.
	May not be NULL.

  @param createIfNotFound [in]:
    Specifies if the property should be created if it does not exist.
  
  @return 
    TX_RESULT_OK: The state value was successfully retrieved.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: The property was not found.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txGetPropertyForStateValue(
    TX_CONSTHANDLE hStateBag,    
    TX_PROPERTYHANDLE* phProperty,
    TX_CONSTSTRING valuePath,
    TX_BOOL createIfNotFound
    );
TX_C_END

/*********************************************************************************************************************/

/**
  txRegisterStateChangedHandler

  Registers a state changed handler.
  This is a helper which automatically registers a notification message handler and a state observer.
 
   @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to listen for state changes.
    Must not be TX_EMPTY_HANDLE.

  @param pTicket [out]: 
    A pointer to a TX_TICKET which will represent this registration. 
    This ticket should be used for unregistration.
    Must not be NULL.

  @param handler [in]: 
    A TX_ASYNCDATACALLBACK which will be called when a state changes.
    Must not be NULL.

  @param userParam [in]: 
    A TX_USERPARAM which will be provided as a parameter to the callback.

  @return 
    TX_RESULT_OK: The Query Handler was successfully registered.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.	
    TX_RESULT_DUPLICATESTATEOBSERVER: A registration for this path already exists.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txRegisterStateChangedHandler(
    TX_CONTEXTHANDLE hContext,
	TX_TICKET* pTicket,
	TX_CONSTSTRING statePath,
	TX_ASYNCDATACALLBACK handler,
	TX_USERPARAM userParam);
TX_C_END

/*********************************************************************************************************************/

#if defined(__cplusplus)
#ifndef TOBII_TX_INTEROP
#include <functional>

    TX_API_FUNCTION_CPP(RegisterStateChangedHandler, (
        TX_CONTEXTHANDLE hContext,
        TX_TICKET* pTicket,
        TX_CONSTSTRING statePath,
        const Tx::AsyncDataCallback& handler));

#endif
#endif

/*********************************************************************************************************************/

/**
  txUnregisterQueryHandler

  Unregisters a previously registered state changed handler callback. 
  
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to unregister the callback.
    Must not be TX_EMPTY_HANDLE.
 
  @param ticket [in]: 
    A TX_TICKET which represents the registration. 
    Must not be TX_INVALID_TICKET
  
  @return 
    TX_RESULT_OK: The callback was successfully unregistered.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
    TX_RESULT_NOTFOUND: A registration for the specified ticket could not be found.
*/ 
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txUnregisterStateChangedHandler(
    TX_CONTEXTHANDLE hContext,
	TX_TICKET ticket);
TX_C_END

/*********************************************************************************************************************/

#endif /* !defined(__TOBII_TX_STATES_API__H__) */

/*********************************************************************************************************************/

