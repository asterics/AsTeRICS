function modelselclassifier(DirInfo,file,data,Cvec,kernel,kerneloptionvec,channelselection,chanselparam)

% USAGE
%
% modelselclassifier(DirInfo,file,data,Cvec,kernel,kerneloptionvec,channelselection,chanselparam)
%
% Performns a big crossvalidation and a variable selection for each 
% classifier of the learning set
% the results are stored in a CV file
%
%
% DirInfo           path of the data
% file                  file.app a cell containing the list of data to be used for
%                       learning
% data              parameters on how the data has been preprocessed. These
%                   parameters are related to the data filename
%                   data.normalizationtype='normal';
%                   data.typedata='allfilt';
%                   data.highcutofffrequency=20;   
%
% Cvec              vector of C values to test
% kernel            kernel name 'gaussian' 'poly'
% kerneloptionvec   kerneloption to test
% channelselection      binary value if channelselection is performed (1)
%                       or not (0) 
%
% channelparamclass     channelselection parameters (those used in
%                       modelclassifier function)
%


lambda=1e-6;
verbose=1;
span=1; 
classcode=[1 -1];

for itercrossval=1:length(file.app);
    
    
    %-------------------------------------------------------------
    %                           Creating Data
    %--------------------------------------------------------------
    
    fileapp=file.app{itercrossval}
    j=1;
    for k=1:length(file.app)
        if k~=itercrossval
            filet{j}=file.app{k};
            
            j=j+1;
        end;
    end;
    xaaux=[];
    yaaux=[];
    
    filen=[DirInfo.pathdata char(fileapp) '-' data.typedata int2str(data.highcutofffrequency) '.mat'];
    load(filen)
    xaaux=[x];
    yaaux=[y];
    
    [xaaux,aux,mnormalize,stdnormalize]=normalize(xaaux,[],yaaux,[],channel,triallength,[],[],data.normalizationtype);
    
    %---------------------------------------------
    % create, read and save test data in temp file and in a single matrix
    %----------------------------------------
    yt=[];
    xtaux=[];
    for filen=filet
        filen=[DirInfo.pathdata char(filen) '-' data.typedata int2str(data.highcutofffrequency)];
        load(filen)
        xtaux2=[x];
        yt=[yt;y]; 
        [aux,xtaux1]=normalize([],xtaux2,[],[],channel,triallength,mnormalize,stdnormalize,data.normalizationtype);
        xtaux=[xtaux;xtaux1];    
    end;
    clear xtaux2,xtaux1;
    %--------------------------------------------------------------------------------------------------------
    
    xa=xaaux;
    alpha=zeros(size(xa,1),1);alpha(1:10)=1;
    for iterC=1:length(Cvec)
        for iterK=1:length(kerneloptionvec)
            
            C=Cvec(iterC);
            
            kerneloption=kerneloptionvec(iterK);            
            if channelselection
                
                %---------------------------------------
                %  channel selection
                %---------------------------------------
                newchannel=1:64;
                nbchannel=length(newchannel);
                lengthperchannel=size(xa,2)/length(newchannel);
                ChannelToSelect=1:nbchannel;
                RankedChannel=[];
                ValueCriterion=[];
                fprintf('Variable Selection :');
                iterchan=1;
                while length(ChannelToSelect)>chanselparam.NbChanMax
                    fprintf('.');
                    Criterion=[];
                    for iterchantoselect=1:length(ChannelToSelect); 
                        
                        fprintf('C :%d /%d sigma : %d /%d  Chan : %d /%d ', iterC,length(Cvec),iterK,length(kerneloptionvec),iterchantoselect, length(ChannelToSelect));
                        
                        
                        ChannelToSelectAux=ChannelToSelect;
                        ChannelToSelectAux(iterchantoselect)=[];
                        % ChannelToSelectAux
                        xaaux=KeepChannel(xa,ChannelToSelectAux,lengthperchannel);
                        
                        [xsup,w,b,pos,timeps,alphaaux]=svmclass(xaaux,yaaux,C,lambda,kernel,kerneloption,verbose,span,alpha);
                        alpha=zeros(size(xa,1),1);alpha(pos)=alphaaux;
                        
                        
                        %--------------------------------------------------
                        %  Read test file and Test
                        %--------------------------------------------------
                        
                        %                         yptest=[];
                        %                         yt=[];
                        %                         for filen=filet
                        %                             filen=[DirInfo.pathdata char(filen) '-' data.typedata int2str(data.highcutofffrequency)];
                        %                             load(filen)
                        %                             xtaux=[x];
                        %                             yt=[yt;y]; 
                        %                             xtaux=KeepChannel(xtaux,ChannelToSelectAux,lengthperchannel);
                        %                             mnormalizeaux=KeepChannel(mnormalize,ChannelToSelectAux,lengthperchannel);
                        %                             stdnormalizeaux=KeepChannel(stdnormalize,ChannelToSelectAux,lengthperchannel);
                        %                             [aux,xtaux]=normalize([],xtaux,[],[],channel,triallength,mnormalizeaux,stdnormalizeaux,data.normalizationtype);
                        %                             yptest=[yptest;svmval(xtaux,xsup,w,b,kernel,kerneloption,span)];
                        %                         end;
                        
                        yptest=svmval(KeepChannel(xtaux,ChannelToSelectAux,lengthperchannel),xsup,w,b,kernel,kerneloption,span);
                        
                        switch chanselparam.criterion  % A criterion to maximize
                            case 'tp'
                                [Conf,metric]=ConfusionMatrix(sign(yptest),yt,classcode);
                                Criterion(iterchantoselect)=Conf(1,1)/(Conf(1,1)+ Conf(2,1)+ Conf(1,2));
                            case 'AUC'
                                Criterion(iterchantoselect)=svmroccurve(yptest,yt);
                                
                        end
                    end; 
                    [value,indtoremove]=sort(-Criterion);
                    RankedChannel=[ ChannelToSelect(indtoremove(1:chanselparam.RemoveChan))  RankedChannel];
                    ChannelToSelect(indtoremove(1:chanselparam.RemoveChan))=[];
                    ValueCriterion=[-value(1) ValueCriterion ];
                    iterchan=iterchan+1;
                end;
                
                ValueMaxCK{iterC,iterK}=ValueCriterion;
                VariableRanking{iterC,iterK}=[ChannelToSelect RankedChannel];
                
            else
                %---------------------------------------
                %  No channel selection
                %---------------------------------------
                if channelselection==0 & isfield(chanselparam,'channel');
                    channel=chanselparam.channel;   
                    lengthperchannel=chanselparam.lengthperchannel;
                end;
                
                xaaux2=KeepChannel(xaaux,channel,lengthperchannel);
                [xsup,w,b,pos,timeps,alphaaux]=svmclass(xaaux2,yaaux,C,lambda,kernel,kerneloption,verbose,span);                
                % yptest=svmval(xt,xsup,w,b,kernel,kerneloption,span);
                
                yptest=[];
                yt=[];
                for filen=filet
                    filen=[DirInfo.pathdata char(filen) '-' data.typedata int2str(data.highcutofffrequency)];
                    load(filen)
                    xtaux=[x];
                    yt=[yt;y]; 
                    [aux,xtaux]=normalize([],xtaux,[],[],channel,triallength,mnormalize,stdnormalize,data.normalizationtype);
                    if channelselection==0 & isfield(chanselparam,'channel');
                    channel=chanselparam.channel;   
                    lengthperchannel=chanselparam.lengthperchannel;
                end;
                    xtaux=KeepChannel(xtaux,channel,lengthperchannel);
                    yptest=[yptest;svmval(xtaux,xsup,w,b,kernel,kerneloption,span)];
                end;
                
                
                [Conf,metric]=ConfusionMatrix(sign(yptest),yt,classcode);
                ValueMaxCK{iterC,iterK}=Conf(1,1)/(Conf(1,1)+ Conf(2,1)+ Conf(1,2));

                restchannel=[];
            end;
            
            
        end;
        save temp.mat
    end;
    % finding the best
    %
    maxi=-inf;
    for iterC=1:length(Cvec)
        for iterK=1:length(kerneloptionvec)
            if maxi < max(max(ValueMaxCK{iterC,iterK}))
                [maxi,indmax] =    max(max(ValueMaxCK{iterC,iterK}));
                iterCmax=iterC;
                iterKmax=iterK;
                if channelselection
                    channel=VariableRanking{iterC,iterK}(1:chanselparam.NbChanMax+(indmax-1)*chanselparam.RemoveChan);
                    restchannel=VariableRanking{iterC,iterK}(chanselparam.NbChanMax+(indmax-1)*chanselparam.RemoveChan+1:end);
                    value= ValueMaxCK{iterC,iterK};
                else 
                    value = ValueMaxCK{iterC,iterK};
                end;
            end;
            
        end;
    end;
    C=Cvec(iterCmax);
    kerneloption=kerneloptionvec(iterKmax);
    
    if channelselection
        filesave=['CV-VS-' char(file.app{itercrossval}) '-crit' chanselparam.criterion '-norm_' data.normalizationtype '-NbChan' int2str(chanselparam.NbChanMax) -'Fc' int2str(data.highcutofffrequency)];
    else
        filesave=['CV-' char(file.app{itercrossval}) '-' data.typedata '-norm_' data.normalizationtype];
    end
    clear xa xt
    filesave = strcat(DirInfo.outputpath,filesave);
    save(filesave,'C','Cvec','data','kernel','kerneloption','value', 'channel','restchannel','lengthperchannel','chanselparam');
end;





