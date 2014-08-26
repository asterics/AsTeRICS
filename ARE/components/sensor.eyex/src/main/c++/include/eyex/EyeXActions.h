/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * EyeXAction.h
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_ACTION_API__H__)
#define __TOBII_TX_ACTION_API__H__

/*********************************************************************************************************************/

/**
  txCreateActionCommand

  Creates an Action command.
 
  @param hContext [in]: 
    A TX_CONTEXTHANDLE to the context on which to create the command.
    Must not be TX_EMPTY_HANDLE.
  
  @param phCommand [out]: 
    A pointer to a TX_HANDLE which will be set to the newly created command.
    This handle must be released using txReleaseObject to avoid leaks.
    Must not be TX_EMPTY_HANDLE.

  @param action [in]:
    The action to send.
 
  @return 
    TX_RESULT_OK: The command was successfully created.
    TX_RESULT_SYSTEMNOTINITIALIZED: The system is not initialized.
    TX_RESULT_INVALIDARGUMENT: An invalid argument was passed to the function.
 */
TX_C_BEGIN
TX_API TX_RESULT TX_CALLCONVENTION txCreateActionCommand(
    TX_CONTEXTHANDLE hContext,
    TX_HANDLE* phCommand,
    TX_ACTIONTYPE action    
    );
TX_C_END

/*********************************************************************************************************************/

#endif /* !defined(__TOBII_TX_ACTION_API__H__) */

/*********************************************************************************************************************/
