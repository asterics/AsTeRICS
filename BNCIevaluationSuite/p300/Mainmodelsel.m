% This script do the model selection classifier 
% the hyperparameters of each classifier should be save 

%

% 16/02

clear all
close all




data.normalizationtype='normal';
data.typedata='allfilt';
data.highcutofffrequency=20;
h = helpdlg('Select directoy with preprocessed data');
uiwait(h);
DirInfo.pathdata=uigetdir('','Select directoy with preprocessed data');
DirInfo.pathdata=strcat(DirInfo.pathdata,'/');
h = helpdlg('Select directoy for output data');
uiwait(h);
outputpath = uigetdir('','Select directoy for output data');
DirInfo.outputpath = strcat(outputpath,'/');

% NO channel selection and just do model selection with C and kerneloption
channelselection=0; 
chanselparam=[];
chanselparam.channel=[34 11 51 9 13 49 53 62];
%chanselparam.channel=[1:64];
chanselparam.lengthperchannel=14;
%%-----------------------------------------
%% channel selection parameter
% channelselection=1;   % if channel has been ranked
% chanselparam.NbChanMax=4; % final number of channel
% chanselparam.RemoveChan=4; % final number of channel
% chanselparam.criterion='tp'; % criterion for channel selection
%-----------------------------------------



Cvec=[0.001 0.005 0.01    0.05    0.1 0.5    1] % tp
kernel='poly';
kerneloptionvec=1;


% The criterion is evaluated on based on a cross-validation procedure

%%%--------------------------------------------------------------------------
%%%                     Model Selection Procedure
%%%------------------------------------------------------------------------
File.app={
    'A1-A2-A3-A4-A5' 'A6-A7-A8-A9-A10' ...
        'A11-A12-A13-A14-A15' 'A16-A17-A18-A19-A20'   ...    
            'A21-A22-A23-A24-A25' 'A26-A27-A28-A29-A30' ...
        'A31-A32-A33-A34-A35' 'A36-A37-A38-A39-A40' ...
        
};    

modelselclassifier(DirInfo,File,data,Cvec,kernel,kerneloptionvec,channelselection,chanselparam);




File.app={ 
    'A76-A77-A78-A79-A80' 'A81-A82-A83-A84-A85'...    
        'A41-A42-A43-A44-A45'  'A46-A47-A48-A49-A50' ...
        'A51-A52-A53-A54-A55'  'A56-A57-A58-A59-A60' ...
        'A61-A62-A63-A64-A65'  'A66-A67-A68-A69-A70' ...
        'A71-A72-A73-A74-A75'   ...
        
};
modelselclassifier(DirInfo,File,data,Cvec,kernel,kerneloptionvec,channelselection,chanselparam);


% --------------------------------------------------------------------------
%       SUBJECT B
% -------------------------------------------------------------------------

File.app={...
          'B36-B37-B38-B39-B40' 'B31-B32-B33-B34-B35' ... 
       'B1-B2-B3-B4-B5' 'B6-B7-B8-B9-B10' ...
        'B11-B12-B13-B14-B15'  'B16-B17-B18-B19-B20' ...
        'B21-B22-B23-B24-B25'  'B26-B27-B28-B29-B30' ...
         
    };

modelselclassifier(DirInfo,File,data,Cvec,kernel,kerneloptionvec,channelselection,chanselparam);
File.app={...
        
        'B76-B77-B78-B79-B80' ...
        'B61-B62-B63-B64-B65'  'B66-B67-B68-B69-B70' ...
        'B51-B52-B53-B54-B55'  'B56-B57-B58-B59-B60' ...      
    'B41-B42-B43-B44-B45'  'B46-B47-B48-B49-B50' ...
    'B71-B72-B73-B74-B75' ...% 'B76-B77-B78-B79-B80' ...
    'B81-B82-B83-B84-B85' ...
} ;
modelselclassifier(DirInfo,File,data,Cvec,kernel,kerneloptionvec,channelselection,chanselparam);