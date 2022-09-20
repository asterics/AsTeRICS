function [word,vote]=TestWord(ypred,codetest,nbiter,method)


% USAGE
%
% [word,vote]=TestWord(ypred,codetest,nbiter,method)
%
% low level function for recognizing a single character
% from the classification of post-stimulus signal
%
% ypred     vector of classification of post stimulus
% codetest  row or columns matrix speller code associated to the post
%           stimulus signal
% nbiter    nb of sequences to used for recognition
% method   different methods for defining the predicted character from the
%          several post-stimulus signal class prediction
%          usually 'sum' ( 'maxvote' is an alternative)

matrix=['ABCDEF';'GHIJKL';'MNOPQR';'STUVWX';'YZ1234';'56789_'];
nbcol=6;
nblig=6;
nbbloc=180;
word='';
nblettre=length(ypred)/nbbloc;
for i=1:nblettre



    indlettre=nbbloc*(i-1)+1:nbbloc*i;
    indlettre=indlettre(1:min((nbcol+nblig)*nbiter,length(indlettre)));
    codelettre=codetest(indlettre);
    yplettre=ypred(indlettre);
    vote=zeros(1,12);
    for k=1:length(indlettre)
        
        % MAXVOTE
        switch method
        case 'maxvote'
            if yplettre(k)>0
                vote(codelettre(k))=vote(codelettre(k))+1;    
            end
        case 'sum'
            
            vote(codelettre(k))=vote(codelettre(k))+yplettre(k);   
        end
        
        
        
    end;
    
    [aux,col]=max(vote(1:nbcol));
    [aux,lig]=max(vote(nbcol+1:nbcol+nblig)); 
    
    word= [word matrix(lig,col)];

end;