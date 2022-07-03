function  [xappf,xtestf,mposxapp,sigmaposxapp]=normalize(xapp,xtest,yapp,ytest,channel,triallength,mposxapp,sigmaposxapp,method)

%  USAGE 
% [xappf,xtestf,mposxapp,sigmaposxapp]=normalize(xapp,xtest,yapp,ytest,channel,triallength,mposxapp,sigmaposxapp,method)
%
% two methods : 'pos' and 'normal'.
% respectively normalized wrt only the positive examples in the learning
% set and all the examples in the learning set
% 

% 16/05/2005

xappf=[];
xtestf=[];
nbapp=size(xapp,1);
nbchannel=length(channel);

if nbapp >0 
    indposapp=find(yapp==1);
    indnegapp=find(yapp==-1);
    
    switch method
    case 'pos'
        if ~isempty(xapp)
            mposxapp=mean(xapp(indposapp,:));
            sigmaposxapp=std(xapp(indposapp,:));
        end;
    case 'normal'
        if ~isempty(xapp)
            mposxapp=mean(xapp);
            sigmaposxapp=std(xapp);
        end;
    end;
    xappf=(xapp -ones(nbapp,1)*mposxapp)./(ones(nbapp,1)*sigmaposxapp);
    
    
    
end;

nbtest=size(xtest,1);

if nbtest~=0
    xtestf=(xtest -ones(nbtest,1)*mposxapp)./(ones(nbtest,1)*sigmaposxapp);
end;