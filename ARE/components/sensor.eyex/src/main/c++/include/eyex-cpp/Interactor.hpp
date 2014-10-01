/*********************************************************************************************************************
 * Copyright 2013-2014 Tobii Technology AB. All rights reserved.
 * Interactor.hpp
 *********************************************************************************************************************/

#if !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__HPP__)
#define __TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__HPP__

/*********************************************************************************************************************/

TX_NAMESPACE_BEGIN

/*********************************************************************************************************************/

class Interactor :
	public InteractionObject
{
public:	
	Interactor(const std::shared_ptr<const Context>& spContext, TX_HANDLE hInteractor);
		
	std::string GetId() const;
	std::string GetParentId() const;
    std::string GetWindowId() const;

    bool GetEnabled() const;
    void SetEnabled(bool enabled);    

    bool GetDeleted() const;
    void SetDeleted(bool deleted);

    double GetZ() const;
    void SetZ(double z);
    
    void CreateGazePointDataBehavior(const TX_GAZEPOINTDATAPARAMS& params);
    void CreateActivatableBehavior(const TX_ACTIVATABLEPARAMS& params);
    void CreatePannableBehavior(const TX_PANNABLEPARAMS& params);    
    void CreateGazeAwareBehavior(const TX_GAZEAWAREPARAMS& params);    
    void CreateFixationDataBehaviorParams(const TX_FIXATIONDATAPARAMS& params); 

    std::shared_ptr<Bounds> GetBounds() const;
    std::vector<std::shared_ptr<Behavior>> GetBehaviors() const;
    std::shared_ptr<Bounds> CreateBounds(TX_BOUNDSTYPE boundsType);
    void DeleteBounds();

    std::shared_ptr<Behavior> CreateBehavior(TX_BEHAVIORTYPE behaviorType);
    void DeleteBehavior(TX_BEHAVIORTYPE behaviorType);
	bool TryGetBehavior(std::shared_ptr<Behavior> *pspBehavior, TX_BEHAVIORTYPE behaviorType) const;

    std::shared_ptr<Mask> CreateMask(TX_MASKTYPE maskType, int columnCount, int rowCount, const TX_BYTE* pData);
    void RemoveMask();
    std::shared_ptr<Mask> GetMask() const;
    
    void SetMaskBounds(const TX_RECT& bounds);
    void ClearMaskBounds();
    bool TryGetMaskBounds(TX_RECT* pBounds) const;
};

/*********************************************************************************************************************/

TX_NAMESPACE_END

/*********************************************************************************************************************/


#endif // !defined(__TOBII_TX_CLIENT_CPPBINDINGS_INTERACTOR__HPP__)

/*********************************************************************************************************************/
