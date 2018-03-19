-- Filter the exampleBundleEssentials Sample Code Section from API Online Integration Guide
UPDATE branding SET filters = filters || E'\nexampleBundleEssentials=HIDE'
WHERE ownertype = 'SITE'
      AND ownerid = 'default'
      AND NOT filters LIKE '%exampleBundleEssentials=HIDE%';


-- Filter the exampleBundleTargeted Sample Code Section from API Online Integration Guide
UPDATE branding SET filters = filters || E'\nexampleBundleTargeted=HIDE'
WHERE ownertype = 'SITE'
      AND ownerid = 'default'
      AND NOT filters LIKE '%exampleBundleTargeted=HIDE%';
